package com.krb.guaranty.common.framework.mvc.enums;

import java.util.LinkedHashMap;
import java.util.Map;

public interface OursBaseStringEnum {
    String getValue();

    String getMessage();

    static <T extends OursBaseStringEnum> T stringValueOf(Class<T> c, String value) {
        if (c != null && c.isEnum()) {
            T[] enums = c.getEnumConstants();
            for (T e: enums) {
                if (e.getValue().equals(value)) {
                    return e;
                }
            }
        }
        return null;
    }

    static <T extends OursBaseStringEnum> String stringEnumToMessage(Class<T> classOfStringEnum, String value){
        T enumValue = OursBaseStringEnum.stringValueOf(classOfStringEnum, value);
        if(enumValue != null){
            return enumValue.getMessage();
        }else{
            return null;
        }
    }

    static <T extends OursBaseStringEnum> Map<String,String> toStringMap(Class<T> c) {
        Map<String,String> map = new LinkedHashMap<>();
        if (c != null && c.isEnum()) {
            T[] enums = c.getEnumConstants();
            for (T e: enums) {
                map.put(e.getValue(),e.getMessage());
            }
        }
        return map;
    }

}