package com.krb.guaranty.common.utils;

/**
 * @author owell
 * @date 2019/8/12 13:35
 */
public class IFCUtil {
    public static <V> V defaultValue(V v,V defaultValue){
        return v == null ? defaultValue:v;
    }
}
