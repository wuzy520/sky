package com.wuzy.sky.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import java.io.IOException;
import java.util.List;

/**
 * Created by apple on 2016/10/5.
 * 服务注册
 */
public class ServiceRegistrar {
    private ServiceDiscovery<InstanceDetails> serviceDiscovery;
    private final CuratorFramework client;

    public ServiceRegistrar(CuratorFramework client, String basePath) throws Exception {
        this.client = client;
        JsonInstanceSerializer<InstanceDetails> serializer = new JsonInstanceSerializer<InstanceDetails>(InstanceDetails.class);
        serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceDetails.class)
                .client(client)
                .serializer(serializer)
                .basePath(basePath)
                .build();
        serviceDiscovery.start();
    }

    /**
     * 注册多个服务
     * @param serviceInstances
     * @throws Exception
     */
    public void registerServices(List<ServiceInstance<InstanceDetails>> serviceInstances)throws Exception{
        for (ServiceInstance<InstanceDetails> serviceInstance:serviceInstances){
            serviceDiscovery.registerService(serviceInstance);
        }
    }

    /**
     * 注册单个服务
     * @param serviceInstance
     * @throws Exception
     */
    public void registerService(ServiceInstance<InstanceDetails> serviceInstance) throws Exception {
        serviceDiscovery.registerService(serviceInstance);
    }

    /**
     *注销多个服务
     * @param serviceInstances
     * @throws Exception
     */
    public void unregisterServices(List<ServiceInstance<InstanceDetails>> serviceInstances)throws Exception{
        for (ServiceInstance<InstanceDetails> serviceInstance:serviceInstances){
            serviceDiscovery.unregisterService(serviceInstance);
        }
    }

    /**
     * 注销单个服务
     * @param serviceInstance
     * @throws Exception
     */
    public void unregisterService(ServiceInstance<InstanceDetails> serviceInstance) throws Exception {
        serviceDiscovery.unregisterService(serviceInstance);

    }

    public void updateService(ServiceInstance<InstanceDetails> serviceInstance) throws Exception {
        serviceDiscovery.updateService(serviceInstance);

    }

    public void close() throws IOException {
        serviceDiscovery.close();
    }
}
