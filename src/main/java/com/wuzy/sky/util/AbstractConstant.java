package com.wuzy.sky.util;

/**
 * Created by apple on 2016/10/2.
 */
public abstract class AbstractConstant {

    private final int id;
    private final String name;

    protected AbstractConstant(int id,String name){
        this.id = id;
        this.name = name;
    }


    public final int getId() {
        return id;
    }

    public final String getName() {
        return name;
    }
}
