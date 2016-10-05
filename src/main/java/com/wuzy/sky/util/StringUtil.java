package com.wuzy.sky.util;

/**
 * Created by apple on 2016/10/5.
 */
public class StringUtil {

    public static boolean isEmpty(String str){
        return str==null || str.trim().equals("");
    }

    public static boolean isNotEmpty(String str){
        return !isEmpty(str);
    }
}
