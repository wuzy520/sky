package com.wuzy.sky.javassist;

import javassist.*;

import java.lang.reflect.Method;

/**
 * Created by apple on 2016/10/15.
 */
public class ClassDemo {

    public static void main(String[] args) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(ClassDemo.class));
        Loader cl = new Loader(pool);



        pool.importPackage("java.util");
        pool.importPackage("com.wuzy.sky.javassist");

        CtClass ctClass = pool.get("com.wuzy.sky.javassist.A");



        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append("Set set=new HashSet(); set.add(\"10002\"); $1.setValues(set);");
        sb.append("}");
        CtMethod ctMethod = ctClass.getDeclaredMethod("set");
        ctMethod.setBody(sb.toString());


        Class clazz = ctClass.toClass();


        Method method = clazz.getMethod("set", Model.class);
        Model model = new Model();
        method.invoke(clazz.newInstance(), model);
        System.out.println(model.getValues());
        //--------------------------------------
        ctClass.defrost();


        StringBuffer sb1 = new StringBuffer();
        sb1.append("{");
        sb1.append("Set set=new HashSet(); set.add(\"10003\"); System.out.println(123);$1.setValues(set);");
        sb1.append("}");
        CtMethod ctMethod1 =ctClass.getDeclaredMethod("set");
       // ctMethod1.setBody(sb1.toString());
        ctMethod1.insertAfter("{System.out.println(123456789);}");




        Method method1 = clazz.getMethod("set", Model.class);
        Model model1 = new Model();
        method1.invoke(clazz.newInstance(), model1);
        System.out.println(model1.getValues());

    }
}
