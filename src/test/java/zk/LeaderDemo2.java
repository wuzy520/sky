package zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

import java.util.ArrayList;
import java.util.List;


public class LeaderDemo2 {
    public static void main(String[] args) throws Exception {
        List<LeaderLatch> leaders = new ArrayList<LeaderLatch>();
        List<CuratorFramework> clients = new ArrayList<CuratorFramework>();

        CuratorFramework client = null;
        LeaderLatch leader = null;
        try {
            //有10台机器参与选举
            client = CuratorFrameworkFactory
                    .builder()
                    .connectString("localhost:2181")
                    .sessionTimeoutMs(3000)
                    .connectionTimeoutMs(5000)
                    .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                    .build();
            clients.add(client);

            leader = new LeaderLatch(client, "/francis/leader");
            leader.addListener(new LeaderLatchListener() {

                @Override
                public void isLeader() {
                    // TODO Auto-generated method stub
                    System.out.println("I am Leader");
                }

                @Override
                public void notLeader() {
                    // TODO Auto-generated method stub
                    System.out.println("I am not Leader");
                }
            });


            leaders.add(leader);

            client.start();
            leader.start();

            Thread.sleep(Integer.MAX_VALUE);
        } finally {
            CloseableUtils.closeQuietly(client);
            CloseableUtils.closeQuietly(leader);
        }


        Thread.sleep(Integer.MAX_VALUE);
    }

}