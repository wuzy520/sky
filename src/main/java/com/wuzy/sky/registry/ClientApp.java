package com.wuzy.sky.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceInstance;

/**
 * Created by apple on 2016/10/5.
 */
public class ClientApp {
    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("localhost:2181")
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();

        ServiceFinder finder = new ServiceFinder(client, "/rpc");
        finder.addListener("com.wuzy.UserDao");

        ServiceInstance<InstanceDetails> serviceInstance = finder.getInstanceByName("com.wuzy.UserDao");
        InstanceDetails details = serviceInstance.getPayload();
        System.out.println(details);
        System.out.println("-------------------------->>>");

        ServiceInstance<InstanceDetails> serviceInstance2 = finder.getInstanceByName("com.wuzy.UserDao");
        InstanceDetails details2 = serviceInstance2.getPayload();
        System.out.println(details2);
        System.out.println("-------------------------->>>");


        ServiceInstance<InstanceDetails> serviceInstance3 = finder.getInstanceByName("com.wuzy.UserDao");
        InstanceDetails details3 = serviceInstance3.getPayload();
        System.out.println(details3);
        System.out.println("-------------------------->>>");


        ServiceInstance<InstanceDetails> serviceInstance4 = finder.getInstanceByName("com.wuzy.UserDao");
        InstanceDetails details4 = serviceInstance4.getPayload();
        System.out.println(details4);
        System.out.println("-------------------------->>>");

        Thread.sleep(Integer.MAX_VALUE);
    }
}
