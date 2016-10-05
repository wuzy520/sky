package zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
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

        ServiceDiscoverer discoverer = new ServiceDiscoverer(client, "rpc-service");

        ServiceInstance<InstanceDetails> instance1 = discoverer.getInstanceByName("service1");

        System.out.println(instance1.getPayload());

        ServiceInstance<InstanceDetails> instance2 = discoverer.getInstanceByName("service1");

        System.out.println(instance2.getPayload());

        ServiceInstance<InstanceDetails> instance3 = discoverer.getInstanceByName("service1");

        System.out.println(instance3.getPayload());

        discoverer.close();
        CloseableUtils.closeQuietly(client);

    }
}
