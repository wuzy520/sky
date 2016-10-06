package com.wuzy.sky.registry;

import com.wuzy.sky.RpcChannel;
import com.wuzy.sky.client.ClientContext;
import com.wuzy.sky.client.RpcClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.*;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.details.ServiceCacheListener;
import org.apache.curator.x.discovery.strategies.RandomStrategy;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;

import java.io.Closeable;
import java.util.*;

/**
 * Created by apple on 2016/10/5.
 * <p>
 * 服务发现者
 */
public class ServiceFinder {
    private ServiceDiscovery<InstanceDetails> serviceDiscovery;
    private Map<String, ServiceProvider<InstanceDetails>> providers = new HashMap<>();
    private List<Closeable> closeableList = new ArrayList<>();
    private Object lock = new Object();

    public ServiceFinder(CuratorFramework client, String basePath) throws Exception {
        JsonInstanceSerializer<InstanceDetails> serializer = new JsonInstanceSerializer<InstanceDetails>(InstanceDetails.class);
        serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceDetails.class)
                .client(client)
                .basePath(basePath)
                .serializer(serializer)
                .build();

        serviceDiscovery.start();
    }


    public ServiceInstance<InstanceDetails> getInstanceByName(String serviceName) throws Exception {
        ServiceProvider<InstanceDetails> provider = providers.get(serviceName);
        if (provider == null) {
            synchronized (lock) {
                provider = providers.get(serviceName);
                if (provider == null) {
                    provider = serviceDiscovery.serviceProviderBuilder().
                            serviceName(serviceName).
                            providerStrategy(new RandomStrategy<>())
                            .build();
                    provider.start();
                    closeableList.add(provider);
                    providers.put(serviceName, provider);
                }
            }
        }

        return provider.getInstance();
    }

    public void addListener(String serviceName) throws Exception {

        final ServiceCache cache = serviceDiscovery.serviceCacheBuilder().name(serviceName).build();
        cache.addListener(new ServiceCacheListener() {

            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
            }

            @Override
            public void cacheChanged() {
                List<ServiceInstance> instances = cache.getInstances();
                if (instances.size() == 0) {
                    closeAndRemoveAllChannels();
                }
                Map<String, RpcChannel> channelMap = ClientContext.channelMap;
                //删除丢失的链接
                removeDisconnected(instances, channelMap);

                //添加
                for (ServiceInstance instance : instances) {
                    if (instance == null) continue;
                    InstanceDetails instanceDetails = (InstanceDetails) instance.getPayload();
                    String interfaceName = instanceDetails.getInterfaceName();
                    RpcChannel channel = channelMap.get(interfaceName);
                    if (channel == null) {
                        //不存在,重新创建连接并加入
                        try {
                            RpcChannel rc = ClientContext.builder().startRpcClient(instanceDetails.getListenAddress(), instanceDetails.getListenPort());
                            channelMap.put(interfaceName, rc);
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                }


            }
        });

        cache.start();

    }

    //删除丢失的链接
    private void removeDisconnected(List<ServiceInstance> instances, Map<String, RpcChannel> channelMap) {
        Set<String> keys = channelMap.keySet();
        Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            String name = it.next();
            int removeFlag = 0;
            for (ServiceInstance instance : instances) {
                //去除相等的,不相等的需要close and  remove
                if (instance.getName().equals(name)) {
                    it.remove();
                    removeFlag = 1;
                    break;
                }
            }

            if (removeFlag == 0) {
                RpcChannel removeChannel = channelMap.remove(name);
                removeChannel.close();
            }

        }
    }


    //关闭所有Channel,并移除掉
    private void closeAndRemoveAllChannels() {
        Map<String, RpcChannel> channelMap = ClientContext.channelMap;
        Collection<RpcChannel> channels = channelMap.values();
        for (RpcChannel channel : channels) {
            channel.close();
        }

        channelMap.clear();

    }


    public synchronized void close() {
        for (Closeable closeable : closeableList) {
            CloseableUtils.closeQuietly(closeable);
        }
    }


}
