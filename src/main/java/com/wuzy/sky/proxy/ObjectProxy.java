package com.wuzy.sky.proxy;

import com.wuzy.sky.ConfigOption;
import com.wuzy.sky.RpcChannel;
import com.wuzy.sky.client.ClientContext;
import com.wuzy.sky.pojo.Request;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;

/**
 * Created by apple on 2016/10/1.
 */
public class ObjectProxy<T> implements InvocationHandler {
    private Class<T> clazz;
    private int waitTimeout;

    private Map<ConfigOption, Object> options;

    public ObjectProxy(Class<T> clazz,Map<ConfigOption, Object> options) {
        this.clazz = clazz;
        this.options = options;
    }

    public ObjectProxy(Class<T> clazz,int waitTimeout,Map<ConfigOption, Object> options) {
        this.clazz = clazz;
        this.waitTimeout = waitTimeout;
        this.options = options;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            if ("equals".equals(name)) {
                return proxy == args[0];
            } else if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(name)) {
                return proxy.getClass().getName() + "@" +
                        Integer.toHexString(System.identityHashCode(proxy)) +
                        ", with InvocationHandler " + this;
            } else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }

        Request request = new Request();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        System.out.println("className====="+request.getClassName());
        //发送给服务端,并返回数据
        RpcChannel channel = ClientContext.channelMap.get(request.getClassName());
        int timeout = 0;
        Integer optionTimeOut =  (Integer)options.get(ConfigOption.WAIT_TIMEOUT);
        if (optionTimeOut!=null){
            timeout = optionTimeOut;
        }

        if (waitTimeout>0){
            timeout = waitTimeout;
        }

        return channel.send(request,timeout);

    }
}
