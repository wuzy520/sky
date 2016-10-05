package com.wuzy.sky.registry;

import com.wuzy.sky.util.HostUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceInstanceBuilder;

/**
 * Created by apple on 2016/10/5.
 */
public class ServerApp1 {
    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("localhost:2181")
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .build();
        client.start();

        ServiceRegistrar registrar = new ServiceRegistrar(client,"/rpc");
        InstanceDetails details = new InstanceDetails();
        details.setListenAddress(HostUtil.getLocalHost());
        details.setListenPort(8000);
        details.setInterfaceName("com.wuzy.UserDao");

        ServiceInstance<InstanceDetails> serviceInstance = ServiceInstance.<InstanceDetails>builder()
                .address(HostUtil.getLocalHost())
                .name("com.wuzy.UserDao")
                .port(8000)
                .payload(details)
                .build();
        registrar.registerService(serviceInstance);

        Thread.sleep(Integer.MAX_VALUE);
    }
}
