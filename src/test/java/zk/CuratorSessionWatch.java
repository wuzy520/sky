package zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * Created by apple on 2016/10/4.
 */
public class CuratorSessionWatch {
    public static void main(String[] args) throws Exception{
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("localhost:2181")
                .connectionTimeoutMs(3000)
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .build();

        client.start();

        //创建节点
       String path =  client.create()
               .creatingParentsIfNeeded()
               .withMode(CreateMode.EPHEMERAL)
               .forPath("/zk-book/c1","fuck".getBytes());

        //设置watch
        final NodeCache cache = new NodeCache(client,path,false);
        cache.start(true);
        cache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("Node update Data: "+new String(cache.getCurrentData().getData()));
            }
        });

        //修改
        client.setData().forPath(path,"fuck you mather".getBytes());
        Thread.sleep(1000);

        client.delete().deletingChildrenIfNeeded().forPath(path);

        Thread.sleep(3000);
    }
}
