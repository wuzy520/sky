package com.wuzy.sky.client;

import com.wuzy.sky.ConfigOption;
import com.wuzy.sky.pojo.Request;
import com.wuzy.sky.pojo.Response;
import com.wuzy.sky.proxy.ObjectProxy;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by apple on 2016/9/28.
 */
public class MessageCallback {

    private Request request;
    private Response response;
    private int waitTimeout;

    private Lock lock = new ReentrantLock();
    private Condition finish = lock.newCondition();

    public MessageCallback(Request request,int waitTimeout){
        this.request = request;
        this.waitTimeout = waitTimeout;
    }

    public Object start() throws InterruptedException {
        try {
            lock.lock();
            //设定一下超时时间，rpc服务器太久没有相应的话，就默认返回空吧。
            if (waitTimeout>0) {
                System.out.println("waitTimeout===="+waitTimeout);
                finish.await(Long.valueOf(waitTimeout),TimeUnit.MILLISECONDS);
            }
            if (this.response != null) {
                return this.response.getResult();
            } else {
                return null;
            }
        } finally {
            lock.unlock();
        }
    }

    public void over(Response reponse) {
        try {
            lock.lock();
            finish.signal();
            this.response = reponse;
        } finally {
            lock.unlock();
        }
    }

}
