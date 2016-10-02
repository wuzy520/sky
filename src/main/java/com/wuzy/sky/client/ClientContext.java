package com.wuzy.sky.client;

import com.wuzy.sky.ConfigOption;
import com.wuzy.sky.proxy.ObjectProxy;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by apple on 2016/9/25.
 */
public class ClientContext {

    private Map<ConfigOption,Object> options = new HashMap<>();
    private Lock lock = new ReentrantLock();
    private ClientContext(){

    }

    public ClientContext start(){
        RpcClient rpcClient = RpcClient.getInstance();
        rpcClient.setServer((String) options.get(ConfigOption.SERVER),(Integer) options.get(ConfigOption.PORT));

        try {
            lock.lock();
            rpcClient.doOpen();
            rpcClient.doConnect();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }finally {
            lock.unlock();
        }

        return this;
    }

    private static final class ClientContextHolder{
        private static final ClientContext clientContext = new ClientContext();
    }

    public ClientContext option(ConfigOption option,Object value){
        options.put(option,value);
        return this;
    }

    public static ClientContext create(){

        return ClientContextHolder.clientContext;
    }



    public  <T> T get(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new ObjectProxy<T>(interfaceClass));
    }


    public final Map<ConfigOption,Object> getOptions(){
        return options;
    }




}
