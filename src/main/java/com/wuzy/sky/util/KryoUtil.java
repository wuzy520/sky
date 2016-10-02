package com.wuzy.sky.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;

/**
 * Created by wuzhengyun on 16/7/16.
 */
public class KryoUtil {

    private static KryoPool pool =null;

    static {

        KryoFactory factory = new KryoFactory() {

            public Kryo create() {

                Kryo kryo = new Kryo();

                return kryo;

            }

        };

        pool =  new KryoPool.Builder(factory).build();

    }
    //序列化
    public static byte[] serial(Object object){
        Kryo kryo = pool.borrow();
        //序列化
        Output output = new Output(1024);
        kryo.writeClassAndObject(output,object);
        output.flush();
        output.close();
        byte[] bytes = output.toBytes();
        return bytes;
    }

    //反序列化
    public static Object deserial(byte[] bytes){
        Kryo kryo = pool.borrow();
        //反序列化
       // ByteArrayInputStream bais = new ByteArrayInputStream(bytes,0,1024);
        Input input = new Input(bytes);
        Object o  = kryo.readClassAndObject(input);
        input.close();
        return o;
    }
}
