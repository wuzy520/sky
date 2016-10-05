package com.wuzy.sky.client;

import com.wuzy.sky.RpcChannel;
import com.wuzy.sky.codec.KryoMsgDecoder;
import com.wuzy.sky.codec.KryoMsgEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.TimeUnit;

/**
 * Created by apple on 2016/10/1.
 */
public class RpcClient {
    private Bootstrap bootstrap;
    private volatile Channel channel; // volatile, please copy reference to use
    private volatile boolean closed;
    private int connectTimeOut;

    private String host;
    private Integer port;

    public RpcClient() {

    }

    public  RpcClient(String host, Integer port) {
        this.host = host;
        this.port = port;
    }


    public void doOpen() {
        bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        final ClientChannelHandler handler = new ClientChannelHandler();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true);
        if (connectTimeOut > 0) {
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeOut);
        }
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline()
                        .addLast("encoder", new KryoMsgEncoder())
                        .addLast("decoder", new KryoMsgDecoder())
                        .addLast("handler", handler);
            }
        });
    }

    public void doConnect() throws Throwable {
        System.out.println("host, port===" + host + "," + port);
        ChannelFuture future = bootstrap.connect(host, port).sync();
        try {
            boolean ret = future.awaitUninterruptibly(5000, TimeUnit.MILLISECONDS);
            System.out.println("ret===" + ret);
            if (ret && future.isSuccess()) {
                Channel newChannel = future.channel();
                try {
                    // 关闭旧的连接
                    Channel oldChannel = RpcClient.this.channel;
                    System.out.println("oldChannel==" + oldChannel);
                    if (oldChannel != null) {
                        oldChannel.close();
                        System.out.println("oldChannle close...");
                        //
                    }
                } finally {
                    //
                    if (isClosed()) {
                        try {
                            newChannel.close();
                            System.out.println("newChannel close...");
                        } finally {
                            RpcClient.this.channel = null;
                        }
                    } else {
                        RpcClient.this.channel = newChannel;
                        System.out.println(" RpcClient.this.channel=====" + RpcClient.this.channel);
                    }
                }
            } else if (future.cause() != null) {
                throw future.cause();
            }
        } finally {
            if (!isConnected()) {
                System.out.println("cancel.....");
                future.cancel(true);
            }

            // close();
        }
    }


    public void close() {
        this.closed = true;
    }


    public boolean isClosed() {
        return closed;
    }

    public boolean isConnected() {
        RpcChannel channel = getChannel();
        if (channel == null)
            return false;
        return channel.isConnected();
    }

    public RpcChannel getChannel() {
        Channel c = channel;
        if (c == null || !c.isOpen()) {
            return null;
        }
        return RpcChannel.getOrAddChannel(c);
    }


    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }
}
