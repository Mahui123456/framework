package com.krb.guaranty.common.framework.mvc.enums;

import java.util.LinkedHashMap;
import java.util.Map;

public interface OursBaseEnum {
    int getCode();

    String getMessage();

    static <T extends OursBaseEnum> T valueOf(Class<T> c, int value) {
        if (c != null && c.isEnum()) {
            T[] enums = c.getEnumConstants();
            for (T e: enums) {
                if (e.getCode() == value) {
                    return e;
                }
            }
        }
        return null;
    }

    static <T extends OursBaseEnum> String enumToMessage(Class<T> classOfEnum, int value){
        T enumValue = OursBaseEnum.valueOf(classOfEnum, value);
        if(enumValue != null){
            return enumValue.getMessage();
        }else{
            return null;
        }
    }

    static <T extends OursBaseEnum> Map<Integer,String> toMap(Class<T> c) {
        Map<Integer,String> map = new LinkedHashMap<>();
        if (c != null && c.isEnum()) {
            T[] enums = c.getEnumConstants();
            for (T e: enums) {
                map.put(e.getCode(),e.getMessage());
            }
        }
        return map;
    }

}