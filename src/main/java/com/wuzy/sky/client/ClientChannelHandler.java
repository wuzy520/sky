package com.wuzy.sky.client;

import com.wuzy.sky.pojo.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by apple on 2016/10/1.
 */
public class ClientChannelHandler extends SimpleChannelInboundHandler<Response>{

   public static final  Map<String,MessageCallback> futureMap = new ConcurrentHashMap<String,MessageCallback>();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
    }


    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Response response) throws Exception {
        String requestId = response.getRequestId();
        MessageCallback callback= futureMap.get(requestId);
        if (callback!=null){
            futureMap.remove(requestId);
            callback.over(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }
}
