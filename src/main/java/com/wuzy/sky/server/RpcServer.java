package com.wuzy.sky.server;

import com.wuzy.sky.codec.KryoMsgDecoder;
import com.wuzy.sky.codec.KryoMsgEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.util.Map;

/**
 * Created by apple on 2016/9/25.
 */
public class RpcServer {
    private ServerBootstrap bootstrap;
    private Channel channel;
    private Map<String,Channel> channelMap;//<ip:port, channel>
    private int port;

    public RpcServer(int port){
        this.port=port;
    }

    public void doOpen()throws Throwable{
        bootstrap = new ServerBootstrap();
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        final ServerChannelHandler handler =  new ServerChannelHandler();
        bootstrap.group(boss,work).channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline()
                        .addLast("decoder",new KryoMsgDecoder())
                        .addLast("encoder",new KryoMsgEncoder())
                        .addLast("handler",handler);
            }
        });
        try {
            // bind
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        }finally {

        }
    }

}
