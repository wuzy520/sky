package zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Created by apple on 2016/10/4.
 */
public class CuratorMaster {
    private static final String masterPath = "/master_select";
    public static void main(String[] args) throws Exception{
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("localhost:2181")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .build();
        client.start();

        LeaderSelector selector = new LeaderSelector(client, masterPath, new LeaderSelectorListenerAdapter() {
            @Override
            public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
                System.out.println("成为master角色");
                //进行业务逻辑操作
                Thread.sleep(3000);
                //释放master权利
                System.out.println("完成master操作,释放master权利");
            }
        });

        selector.autoRequeue();
        selector.start();
        Thread.sleep(5000000000000000L);
    }
}
