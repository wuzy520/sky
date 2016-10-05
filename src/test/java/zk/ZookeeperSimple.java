package zk;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

/**
 * Created by apple on 2016/10/3.
 */
public class ZookeeperSimple implements Watcher {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        ZooKeeper zookeeper = new ZooKeeper("localhost:2181",5000,new ZookeeperSimple());
        System.out.println(zookeeper.getState());

        connectedSemaphore.await();
        String path1 = zookeeper.create("/zk-test-","fuck your mother".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println("path1: "+path1);

        String path2 = zookeeper.create("/zk-test-","dead".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("path2: "+path2);



    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("received watch : "+watchedEvent);
        if (Event.KeeperState.SyncConnected==watchedEvent.getState()){
            connectedSemaphore.countDown();
        }
    }
}
