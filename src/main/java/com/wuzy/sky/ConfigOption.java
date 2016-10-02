package com.wuzy.sky;


import com.wuzy.sky.util.AbstractConstant;
import com.wuzy.sky.util.ConstantPool;

/**
 * Created by apple on 2016/10/2.
 */
public final  class ConfigOption extends AbstractConstant{

    private ConfigOption(int id,String name){
        super(id, name);
    }

    private static final ConstantPool<ConfigOption>  pool = new ConstantPool<ConfigOption>() {
        @Override
        protected ConfigOption newConstant(int id, String name) {
            return new ConfigOption(id,name);
        }
    };

    public static final ConfigOption PORT = valueOf("PORT");//端口号
    public static final ConfigOption SERVER = valueOf("SERVER");//服务器地址
    public static final ConfigOption ZK = valueOf("ZK");//zk地址
    public static final ConfigOption WAIT_TIMEOUT = valueOf("WAIT_TIMEOUT");//等待服务器返回时间,单位毫秒
    public static final ConfigOption CONNECT_TIMEOUT_MILLIS = valueOf("CONNECT_TIMEOUT_MILLIS");//最大连接服务器时间



    private static ConfigOption valueOf(String name) {
        return pool.valueOf(name);
    }


}
