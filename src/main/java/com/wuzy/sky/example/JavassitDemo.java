package com.wuzy.sky.example;

import javassist.*;

import java.lang.reflect.Method;

/**
 * Created by apple on 2016/10/3.
 */
public class JavassitDemo {

    public static void main(String[] args) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.makeClass("Point");
        String function = "public int get(){return 100;}";
        CtMethod method = CtNewMethod.make(function, cc);
        cc.addMethod(method);
        Class clazz = cc.toClass();
        Object obj = clazz.newInstance();

        Method m = clazz.getMethod("get");
        Object o = m.invoke(obj);
        System.out.println(o);
    }
}
