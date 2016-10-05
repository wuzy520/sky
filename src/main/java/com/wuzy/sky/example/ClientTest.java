package com.wuzy.sky.example;

import com.wuzy.sky.ConfigOption;
import com.wuzy.sky.client.ClientContext;
import java.util.concurrent.CountDownLatch;

/**
 * Created by apple on 2016/10/1.
 */
public class ClientTest {

    private  static int j=0;
    public static void main(String[] args) throws Exception {
        ClientContext clientContext = ClientContext.builder()
                .option(ConfigOption.WAIT_TIMEOUT, 1000)//最大等待服务器时间为1000毫秒
                .option(ConfigOption.CONNECT_TIMEOUT_MILLIS,3000)//最大等待连接时间3000毫秒
                .registry("localhost:2181")//zookeeper 注册中心
                .addService(UserDao.class);//注册要使用的服务接口

        //启动服务,启动服务在实际使用中应该初始化一次。
        clientContext.start();
        UserDao a = clientContext.get(UserDao.class);

        CountDownLatch downLatch = new CountDownLatch(10);
        for (int i=0;i<10;i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String ret = a.findById("hh=="+(j++));
                    System.out.println("ret====" + ret);
                    downLatch.countDown();
                }
            }).start();
        }

        downLatch.await();

       // Thread.sleep(Integer.MAX_VALUE);
    }
}
