package zk;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * Created by apple on 2016/10/5.
 */
public class RecipesNoLock {
    public static void main(String[] args) throws InterruptedException {
        final CountDownLatch down = new CountDownLatch(10);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");

        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String orderNo = sdf.format(new Date());
                    System.out.println("生成的订单号: " + orderNo);
                    down.countDown();
                }
            }).start();

        }

        down.await();

    }
}
