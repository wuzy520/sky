package zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceInstance;

/**
 * Created by apple on 2016/10/5.
 */
public class ServerApp {
    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("localhost:2181")
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .build();
        client.start();

        //服务注册
        ServiceRegistrar serviceRegistrar = new ServiceRegistrar(client,"rpc-service");

        InstanceDetails details = new InstanceDetails();
        details.setId("1");
        details.setInterfaceName("com.wuzy.UserDao");
        details.setListenAddress("localhost");
        details.setListenPort(8000);

        InstanceDetails details2 = new InstanceDetails();
        details2.setId("2");
        details2.setInterfaceName("com.wuzy.UserDao");
        details2.setListenAddress("localhost");
        details2.setListenPort(8001);

        ServiceInstance<InstanceDetails> serviceInstance = ServiceInstance
                .<InstanceDetails>builder()
                .name("service1")
                .port(8000)
                .address("localhost")   //address不写的话，会取本地ip
                .payload(details)
                .build();


        ServiceInstance<InstanceDetails> serviceInstance2 = ServiceInstance
                .<InstanceDetails>builder()
                .name("service1")
                .port(8001)
                .address("localhost")   //address不写的话，会取本地ip
                .payload(details2)
                .build();


        serviceRegistrar.registerService(serviceInstance);
        serviceRegistrar.registerService(serviceInstance2);

        Thread.sleep(Integer.MAX_VALUE);
    }
}
