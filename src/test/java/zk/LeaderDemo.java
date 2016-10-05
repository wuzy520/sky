package zk;

import java.util.ArrayList;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;


public class LeaderDemo {
    public static void main(String[]args) throws Exception{
        List<LeaderLatch>leaders=new ArrayList<LeaderLatch>();
        List<CuratorFramework>clients=new ArrayList<CuratorFramework>();


        
        try{
            //有10台机器参与选举
            for(int i=0;i<1;i++){
              CuratorFramework client=CuratorFrameworkFactory
                      .builder()
                      .connectString("localhost:2181")
                      .sessionTimeoutMs(3000)
                      .connectionTimeoutMs(5000)
                      .retryPolicy(new ExponentialBackoffRetry(1000,3))
                      .build();
              clients.add(client);
              
              LeaderLatch leader=new LeaderLatch(client,"/francis/leader");
              leader.addListener(new LeaderLatchListener(){

                @Override
                public void isLeader() {
                    // TODO Auto-generated method stub
                    System.out.println("I am Leader");
                }

                @Override
                public void notLeader() {
                    // TODO Auto-generated method stub
                    System.out.println("I am not Leader");
                }});
              
              
              leaders.add(leader);
        
              client.start();
              leader.start();
            }
            
            Thread.sleep(Integer.MAX_VALUE);
        }finally{
            
            for(CuratorFramework client:clients){
              CloseableUtils.closeQuietly(client);    
            }
            
            for(LeaderLatch leader:leaders){
                CloseableUtils.closeQuietly(leader);
            }
            
        }
        
        
        Thread.sleep(Integer.MAX_VALUE);
    }

}