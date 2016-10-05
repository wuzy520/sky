package com.wuzy.sky.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.*;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.details.ServiceCacheListener;
import org.apache.curator.x.discovery.strategies.RandomStrategy;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by apple on 2016/10/5.
 *
 * 服务发现者
 */
public class ServiceFinder {
    private ServiceDiscovery<InstanceDetails> serviceDiscovery;
    private Map<String, ServiceProvider<InstanceDetails>> providers =new HashMap<>();
    private List<Closeable> closeableList = new ArrayList<>();
    private Object lock = new Object();

    public ServiceFinder(CuratorFramework client,String basePath) throws Exception {
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

        final ServiceCache cache=serviceDiscovery.serviceCacheBuilder().name(serviceName).build();
        cache.addListener(new ServiceCacheListener(){

            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                System.out.println("stateChanged===="+connectionState.name());
            }

            @Override
            public void cacheChanged() {
                System.out.println("changed====="+cache.getInstances().size());
               List<ServiceInstance> instances =  cache.getInstances();
                for (ServiceInstance instance:instances){
                    System.out.println(instance.getPayload());
                }
            }
        });

        cache.start();

    }


    public synchronized void close(){
        for (Closeable closeable : closeableList) {
             CloseableUtils.closeQuietly(closeable);
        }
    }



}
