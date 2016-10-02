package com.wuzy.sky.proxy;

import com.wuzy.sky.ConfigOption;
import com.wuzy.sky.RpcChannel;
import com.wuzy.sky.client.ClientContext;
import com.wuzy.sky.client.RpcClient;
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

    private Map<ConfigOption, Object> options = ClientContext.create().getOptions();

    public ObjectProxy(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long start = System.currentTimeMillis();
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

        //发送给服务端,并返回数据
        RpcChannel channel = RpcClient.getInstance().getChannel();
        Object result = channel.send(request);

        return result;
    }
}
