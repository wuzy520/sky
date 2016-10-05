package com.wuzy.sky.server;

import com.wuzy.sky.ConfigOption;
import com.wuzy.sky.exception.ServiceException;
import com.wuzy.sky.registry.InstanceDetails;
import com.wuzy.sky.registry.ServiceRegistrar;
import com.wuzy.sky.server.iface.IServiceInitializer;
import com.wuzy.sky.util.HostUtil;
import com.wuzy.sky.util.StringUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.*;

/**
 * Created by apple on 2016/10/2.
 */
public class ServerContext {

    private static IServiceInitializer serviceInitializer;
    private int port;
    private RpcServer rpcServer;
    private String connectStr;//zookeeper注册中心
    private ServiceRegistrar serviceRegistrar;//服务注册
    private CuratorFramework client;
    public static final String basePath = "/sky-rpc";

    //有顺序的Map
    private final Map<ConfigOption, Object> options = new LinkedHashMap();

    //注册的服务
    private Set<Class> serviceClasses = new HashSet<>();

    private static class ServerContextHolder {
        private static final ServerContext context = new ServerContext();
    }

    public static ServerContext create(IServiceInitializer serviceInitializer) {
        if (ServerContext.serviceInitializer == null) {
            synchronized (ServerContext.class) {
                ServerContext.serviceInitializer = serviceInitializer;
            }
        }
        return ServerContextHolder.context;
    }

    public static final IServiceInitializer getServiceInitializer() {
        return ServerContext.serviceInitializer;
    }

    /**
     * 参数配置
     *
     * @param option
     * @param value
     * @return
     */
    public ServerContext option(ConfigOption option, Object value) {
        options.put(option, value);
        return this;
    }

    /**
     * 添加注册接口
     *
     * @param clazzes
     * @return
     */
    public ServerContext addService(Class... clazzes) {
        for (Class clazz : clazzes) {
            serviceClasses.add(clazz);
        }
        return this;
    }

    /**
     * zookeeper的注册中心
     *
     * @param connectStr
     * @return
     */
    public ServerContext registry(String connectStr) {
        this.connectStr = connectStr;
        return this;
    }

    public ServerContext bind(int port) {
        this.port = port;
        return this;
    }

    /**
     * 启动服务
     *
     * @throws ServiceException
     */
    public void start() throws ServiceException {
        if (serviceClasses.size() < 1) {
            throw new ServiceException("注册服务的接口,不能为空!");
        }
        //
        serviceInitializer.init();
        rpcServer = new RpcServer(port);
        try {
            if (StringUtil.isNotEmpty(connectStr)) {
                //服务注册
                initServiceRegistr();
            }
            //连接
            rpcServer.doOpen();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * 服务注册
     * @throws Exception
     */
    private void initServiceRegistr() throws Exception {
        client = CuratorFrameworkFactory.builder()
                .connectString(connectStr)
                .connectionTimeoutMs(5000)
                .sessionTimeoutMs(3000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();
        serviceRegistrar = new ServiceRegistrar(client, basePath);
        //注册服务
        List<ServiceInstance<InstanceDetails>> serviceInstanceList = createServiceInstanceList();
        serviceRegistrar.registerServices(serviceInstanceList);
    }

    private List<ServiceInstance<InstanceDetails>> createServiceInstanceList() throws Exception {
        List<ServiceInstance<InstanceDetails>> serviceInstanceList = new ArrayList<>();
        for (Class clazz : serviceClasses) {
            InstanceDetails details = new InstanceDetails();
            details.setInterfaceName(clazz.getName());
            details.setListenPort(port);
            details.setListenAddress(HostUtil.getLocalHost());
            details.setId(generateId(clazz, HostUtil.getLocalHost(), port));

            System.out.println("clazz.getName() ===== "+clazz.getName());
            ServiceInstance<InstanceDetails> instances = ServiceInstance.<InstanceDetails>builder()
                    .name(clazz.getName())
                    .port(port)
                    .address(details.getListenAddress())
                    .payload(details)
                    .build();
            serviceInstanceList.add(instances);
        }
        return serviceInstanceList;
    }

    //生成id
    private String generateId(Class clazz, String localHost, int port) {
        return clazz.getName() + "-" + localHost + "-" + port;
    }

}
