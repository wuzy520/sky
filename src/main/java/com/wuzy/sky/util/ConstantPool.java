package com.wuzy.sky.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by apple on 2016/10/2.
 */
public abstract class ConstantPool<T> {

    private final Map<String, T> constants = new HashMap<>();
    private int nextId = 1;

    public T valueOf(String name){
        if (name==null){
            throw new NullPointerException("name");
        }else if(name.isEmpty()) {
            throw new IllegalArgumentException("empty name");
        }else{
            synchronized(this.constants) {
               T t= this.constants.get(name);
                if (t==null){
                    t=this.newConstant(this.nextId, name);
                    this.constants.put(name, t);
                    ++this.nextId;
                }

                return t;
            }
        }

    }

    protected abstract T newConstant(int id, String name);

}
