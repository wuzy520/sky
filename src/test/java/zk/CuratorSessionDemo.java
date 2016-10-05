package zk;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * Created by apple on 2016/10/4.
 */
public class CuratorSessionDemo {
    public static void main(String[] args) throws Exception {

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("localhost:2181")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(retryPolicy)
                .build();
        client.start();

        //创建节点
        String path = client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath("/zk-b/c1", "init".getBytes());
        System.out.println(path);

        //删除节点
        Stat stat = new Stat();
        System.out.println("stat===" + stat);

        //获取数据内容,传入一个旧的stat变量来返回最新的节点状态的信息
        byte[] bs = client.getData().storingStatIn(stat).forPath(path);
        System.out.println("bytes: " + new String(bs));
        System.out.println("start22===" + stat);

        //更新数据
       Stat s =  client.setData().withVersion(stat.getVersion()).forPath(path,"nihao".getBytes());
        System.out.println("s==="+s);

        //删除数据
        client.delete().deletingChildrenIfNeeded().withVersion(s.getVersion()).forPath(path);

        System.out.println("stat333===" + s);

    }

    private static void newClient(RetryPolicy retryPolicy) {
        CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181", 5000, 3000, retryPolicy);
        client.start();
    }
}
