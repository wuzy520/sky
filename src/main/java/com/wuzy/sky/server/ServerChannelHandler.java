package com.wuzy.sky.server;

import com.wuzy.sky.pojo.Request;
import com.wuzy.sky.pojo.Response;
import com.wuzy.sky.server.iface.IServiceInitializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by apple on 2016/10/1.
 */
@ChannelHandler.Sharable
public class ServerChannelHandler extends SimpleChannelInboundHandler<Request> {
    public static final ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2+1);
    private static final Map<String, Channel> channelMap = new ConcurrentHashMap<String, Channel>();

    @Override
    protected void messageReceived(final ChannelHandlerContext ctx, final Request request) throws Exception {
        service.submit(new Runnable() {
            @Override
            public void run() {
                Response response = new Response();
                response.setRequestId(request.getRequestId());
                try {
                    Object result = handle(request);
                    response.setResult(result);
                } catch (Throwable t) {
                    t.printStackTrace();
                }

                ctx.writeAndFlush(response).addListener(new GenericFutureListener<Future<? super Void>>() {
                    @Override
                    public void operationComplete(Future<? super Void> future) throws Exception {
                        System.out.println("sucess........");
                    }
                });
            }
        });
    }

    private Object handle(Request request) throws Throwable {
        long start = System.currentTimeMillis();

        IServiceInitializer iServiceInitializer = ServerContext.getServiceInitializer();

        String className = request.getClassName();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        Class clazz = Class.forName(className);
        Object clazzObj = iServiceInitializer.getImpl(clazz);

        //Cglib reflect
       /* FastClass serviceFastClass = FastClass.create(clazz);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        Object obj =  serviceFastMethod.invoke(clazzObj, parameters);
        System.out.println("obj====="+obj);
        */
        Method method = clazz.getMethod(methodName, parameterTypes);
        Object obj = method.invoke(clazzObj, parameters);
        System.out.println("method invoke===" + (System.currentTimeMillis() - start) + " 毫秒");
        return obj;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
    }
}
