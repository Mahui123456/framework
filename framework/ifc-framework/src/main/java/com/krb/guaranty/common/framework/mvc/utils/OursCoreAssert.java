package com.krb.guaranty.common.framework.mvc.utils;

import com.krb.guaranty.common.framework.exception.ValidException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;

public class OursCoreAssert{
    public static final String ASSERT_PREFIX = "";

    public static void notEmpty(Map map, String message){
        if(map==null||map.size()<=0){
            throw new ValidException(ASSERT_PREFIX+message);
        }
    }

    public static void empty(Map map,String message){
        if(map!=null&&map.size()>0){
            throw new ValidException(ASSERT_PREFIX+message);
        }
    }

    public static void notEmpty(Collection collection, String message){
        if(CollectionUtils.isEmpty(collection)){
            throw new ValidException(ASSERT_PREFIX+message);
        }
    }

    public static void empty(Collection collection,String message){
        if(!CollectionUtils.isEmpty(collection)){
            throw new ValidException(ASSERT_PREFIX+message);
        }
    }

    public static void notEmpty(String key,String message){
        if(StringUtils.isEmpty(key)){
            throw new ValidException(ASSERT_PREFIX+message);
        }
    }

    public static void empty(String key,String message){
        if(StringUtils.isNotEmpty(key)){
            throw new ValidException(ASSERT_PREFIX+message);
        }
    }

    public static void notBlank(String key, String message) {
        if(StringUtils.isBlank(key)){
            throw new ValidException(ASSERT_PREFIX+message);
        }
    }

    public static void blank(String key, String message) {
        if(StringUtils.isNotBlank(key)){
            throw new ValidException(ASSERT_PREFIX+message);
        }
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new ValidException(ASSERT_PREFIX+message);
        }
    }

    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new ValidException(ASSERT_PREFIX+message);
        }
    }

    /**
     * if (bo){continue} else {throw message}
     * @param bo
     * @param message
     */
    public static void isTrue(boolean bo, String message){
        if(!bo){
            throw new ValidException(ASSERT_PREFIX+message);
        }
    }

    /**
     * if (!bo){continue} else {throw message}
     * @param bo
     * @param message
     */
    public static void isFalse(boolean bo, String message){
        if(bo){
            throw new ValidException(ASSERT_PREFIX+message);
        }
    }
}