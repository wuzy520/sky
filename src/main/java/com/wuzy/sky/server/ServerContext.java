package com.wuzy.sky.server;

import com.wuzy.sky.ConfigOption;
import com.wuzy.sky.server.iface.IServiceInitializer;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by apple on 2016/10/2.
 */
public class ServerContext {

    private static  IServiceInitializer serviceInitializer;
    private int port;
    private RpcServer rpcServer;

    //有顺序的Map
    private final Map<ConfigOption, Object> options = new LinkedHashMap();

    private  static  class ServerContextHolder{
         private  static  final ServerContext context = new ServerContext();
    }

    public static ServerContext create(IServiceInitializer serviceInitializer){
       if (ServerContext.serviceInitializer==null) {
           synchronized (ServerContext.class) {
               ServerContext.serviceInitializer = serviceInitializer;
           }
       }
        return ServerContextHolder.context;
    }

    public static final IServiceInitializer getServiceInitializer(){
        return ServerContext.serviceInitializer;
    }

    public ServerContext option(ConfigOption option,Object value){
        options.put(option,value);
        return this;
    }

    public ServerContext port(int port){
        this.port = port;
        return this;
    }


    public void start(){
        //
        serviceInitializer.init();
        rpcServer = new RpcServer(port);
        try {
            //连接
            rpcServer.doOpen();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
