package zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * Created by apple on 2016/10/4.
 */
public class CuratorPathChildCache {
    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("localhost:2181")
                .connectionTimeoutMs(3000)
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        String path ="/zk-xx";

        client.start();

        //对子节点进行监控
        PathChildrenCache cache = new PathChildrenCache(client,path,true);
        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                switch (pathChildrenCacheEvent.getType()){
                    case CHILD_ADDED:
                        System.out.println("add: "+pathChildrenCacheEvent.getData().getPath());
                        break;
                    case CHILD_UPDATED:
                        System.out.println("update: "+pathChildrenCacheEvent.getData().getPath());
                        break;
                    case CHILD_REMOVED:
                        System.out.println("remove: "+pathChildrenCacheEvent.getData().getPath());
                        break;
                }
            }
        });


        String father = client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, "fff".getBytes());
        Thread.sleep(1000);

        client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path+"/c1","xxx".getBytes());
        Thread.sleep(1000);

        client.setData().forPath(path+"/c1","xxx".getBytes());
        Thread.sleep(1000);

        client.delete().forPath(path+"/c1");
        Thread.sleep(1000);
        client.delete().forPath(path);
    }
}
