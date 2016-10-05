package zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * Created by apple on 2016/10/5.
 */
public class RecipesLock {

    public static void main(String[] args) throws Exception {
        String lockPath="/lock_path";
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("localhost:2181")
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .build();
        client.start();

        final InterProcessMutex lock = new InterProcessMutex(client,lockPath);
        CountDownLatch down = new CountDownLatch(1);
        for (int i=0;i<30;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        down.await();
                        lock.acquire();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
                        String orderNo = sdf.format(new Date());
                        System.out.println("生成的订单号: "+orderNo);
                        lock.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();

        }

        down.countDown();
    }
}
