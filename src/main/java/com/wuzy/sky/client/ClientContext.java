package com.wuzy.sky.client;

import com.wuzy.sky.ConfigOption;
import com.wuzy.sky.RpcChannel;
import com.wuzy.sky.exception.ServiceException;
import com.wuzy.sky.proxy.ObjectProxy;
import com.wuzy.sky.registry.InstanceDetails;
import com.wuzy.sky.registry.ServiceFinder;
import com.wuzy.sky.server.ServerContext;
import com.wuzy.sky.util.StringUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceInstance;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by apple on 2016/9/25.
 */
public class ClientContext {

    private final Map<ConfigOption, Object> options = new HashMap<>();
    private Lock lock = new ReentrantLock();
    private String connectStr;//zookeeper 注册服务中心

    private Set<Class> serviceClasses = new HashSet<>();

    private ServiceFinder finder;//服务发现者
    private CuratorFramework client;

    private Set<InstanceDetails> instances = new HashSet<>();

    //保存接口服务名称和RpcChannel的映射关系
    public static final Map<String, RpcChannel> channelMap = new ConcurrentHashMap<>();

    private String host;
    private int port;

    private ClientContext() {

    }



    /**
     * zookeeper 服务注册中心
     *
     * @param connectStr
     * @return
     */
    public ClientContext registry(String connectStr) {
        this.connectStr = connectStr;
        return this;
    }

    private static final class ClientContextHolder {
        private static final ClientContext clientContext = new ClientContext();
    }

    public ClientContext option(ConfigOption option, Object value) {
        options.put(option, value);
        return this;
    }

    /**
     * 添加需要访问的接口
     *
     * @param clazzes
     * @return
     */
    public ClientContext addService(Class... clazzes) {
        for (Class clazz : clazzes) {
            serviceClasses.add(clazz);
        }
        return this;
    }

    /**
     * 连接服务器,生产环境中建议使用注册中心
     * @param host
     * @param port
     * @return
     */
    public ClientContext connect(String host,int port){
        this.host = host;
        this.port = port;
        return this;
    }

    public static ClientContext builder() {
        return ClientContextHolder.clientContext;
    }

    /**
     * 启动服务,
     * 建议使用注册中心
     * @throws Exception
     */
    public void start() throws Exception {
        if (serviceClasses.size() < 1) {
            throw new ServiceException("注册服务的接口,不能为空!");
        }

        if (StringUtil.isNotEmpty(connectStr)) {
            //初始化zookeeper服务发现
            initRegistry();
            //监听zookeeper服务,当服务新增或者移除的时候,需要及时创建相应Channel和移除Channel
            initListener();
            //初始化Channel
            initChannels();
        }else{
            //没有使用注册中心的时候,初始化channel
            initWithoutRegistryChannel(host,port);
        }

    }



    //没有使用注册中心的时候,初始化channel
    private void initWithoutRegistryChannel(String host, int port) throws ServiceException {
        if (StringUtil.isEmpty(host)) {
            throw new NullPointerException("host 不能为空");
        }

        if (port < 0) {
            throw new ServiceException("端口号不合法");
        }

        try {
            RpcChannel channel = startRpcClient(host, port);
            for (Class serviceClass : serviceClasses) {
                channelMap.put(serviceClass.getName(), channel);
            }
        } catch (Throwable throwable) {
            //
            throwable.printStackTrace();
            System.exit(0);
        }
    }


    public <T> T get(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new ObjectProxy<T>(interfaceClass,options));
    }

    public <T> T get(Class<T> interfaceClass, int waitTimeout) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new ObjectProxy<T>(interfaceClass, waitTimeout,options));
    }


    //初始化Channel
    private void initChannels() {
        for (InstanceDetails instance : instances) {
            String host = instance.getListenAddress();
            int port = instance.getListenPort();
            String interfaceName = instance.getInterfaceName();

            try {
                RpcChannel channel = startRpcClient(host, port);
                channelMap.put(interfaceName, channel);
            } catch (Throwable throwable) {
                //连接出错!
                throwable.printStackTrace();
                System.exit(0);
                break;
            }
        }
    }

    public RpcChannel startRpcClient(String host, int port) throws Throwable {
        RpcClient rpcClient = new RpcClient(host, port);
        Object connectTimeout = options.get(ConfigOption.CONNECT_TIMEOUT_MILLIS);
        if (connectTimeout != null) {
            rpcClient.setConnectTimeOut((Integer) connectTimeout);
        }
        try {
            lock.lock();
            rpcClient.doOpen();
            rpcClient.doConnect();
        } finally {
            lock.unlock();
        }

        return rpcClient.getChannel();
    }


    //监听zookeeper服务,当服务新增或者移除的时候,需要及时创建相应Channel和移除Channel
    private void initListener() throws Exception {
        for (InstanceDetails instance : instances) {
            finder.addListener(instance.getInterfaceName());
        }

    }


    //初始化zookeeper服务发现
    private void initRegistry() throws Exception {

        client = CuratorFrameworkFactory.builder()
                .connectString(connectStr)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .connectionTimeoutMs(5000)
                .sessionTimeoutMs(3000)
                .build();
        client.start();
        finder = new ServiceFinder(client, ServerContext.basePath);
        finder.setClientContext(this);

        //记录在zookeeper服务中没有找到的接口服务
        Set<String> errServices = new HashSet<>();
        for (Class clazz : serviceClasses) {
            ServiceInstance<InstanceDetails> serviceInstance = finder.getInstanceByName(clazz.getName());
            if (serviceInstance == null) {
                errServices.add(clazz.getName());
                continue;
            }
            InstanceDetails instanceDetails = serviceInstance.getPayload();
            if (instanceDetails == null) {
                errServices.add(clazz.getName());
            } else {
                instances.add(instanceDetails);
            }
        }

        if (errServices.size() > 0) {
            throw new ServiceException(errServices + ",在zookeeper注册中心中,没有找到这些服务!");
        }
    }


    public final Map<ConfigOption, Object> getOptions() {
        return options;
    }

    public Set<Class> getServiceClasses() {
        return serviceClasses;
    }
}
