package com.wuzy.sky;

import com.wuzy.sky.client.ClientChannelHandler;
import com.wuzy.sky.client.MessageCallback;
import com.wuzy.sky.pojo.Request;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by apple on 2016/10/2.
 */
public class RpcChannel {

    private static final Map<Channel,RpcChannel> channelMap = new ConcurrentHashMap<>();
    private final Channel channel;

    private RpcChannel(Channel channel){
        if (channel == null) {
            throw new IllegalArgumentException("netty channel == null;");
        }
        this.channel = channel;
    }

   public static RpcChannel getOrAddChannel(Channel ch){
        if (ch==null){
            return null;
        }

        RpcChannel rpcChannel = channelMap.get(ch);
        if (rpcChannel==null) {
            RpcChannel rc = new RpcChannel(ch);
            if (ch.isOpen()) {
                rpcChannel = channelMap.putIfAbsent(ch,rc);
            }

            if (rpcChannel==null){
                rpcChannel = rc;
            }
        }

        return rpcChannel;
    }

    public Object send(Request request){
        MessageCallback rpcFuture = new MessageCallback(request);
        ClientChannelHandler.futureMap.put(request.getRequestId(), rpcFuture);
        channel.writeAndFlush(request);
        Object obj = null;
        try {
            obj = rpcFuture.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return obj;
    }


   public static void removeChannelIfDisconnected(Channel ch){
        if (ch!=null && !ch.isOpen()){
            channelMap.remove(ch);
        }
    }

    public InetSocketAddress getLocalAddress(){
        return (InetSocketAddress) channel.localAddress();
    }

    public InetSocketAddress getRemoteAddress(){
        return (InetSocketAddress)channel.remoteAddress();
    }

    public boolean isConnected(){
        return channel.isOpen();
    }

    public void close() {
        try {
            removeChannelIfDisconnected(channel);
        } catch (Exception e) {
           e.printStackTrace();
        }

        try {
            channel.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RpcChannel that = (RpcChannel) o;

        return channel != null ? channel.equals(that.channel) : that.channel == null;

    }

    @Override
    public int hashCode() {
        return channel != null ? channel.hashCode() : 0;
    }
}
