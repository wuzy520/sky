package com.wuzy.sky.server.iface;

/**
 * Created by apple on 2016/10/2.
 *
 * 用来初始化业务类
 */
public interface IServiceInitializer {

    default void init(){

    }

    default Object getImpl(Class<?> iface){
        return null;
    }
}
