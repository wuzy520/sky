package com.wuzy.sky.example;

import com.wuzy.sky.ConfigOption;
import com.wuzy.sky.client.ClientContext;

/**
 * Created by apple on 2016/10/1.
 */
public class ClientTest {

    public static void main(String[] args) throws InterruptedException {

        ClientContext clientContext = ClientContext.create()
                .option(ConfigOption.SERVER, "localhost")//RPC 服务器地址
                .option(ConfigOption.PORT, 8000)//RPC 服务器端口号
                .option(ConfigOption.WAIT_TIMEOUT, 1000)//最大等待服务器时间为100毫秒
                .start();


        UserDao a = clientContext.get(UserDao.class);
        String ret = a.findById("100");
        System.out.println("ret===="+ret);
    }
}
