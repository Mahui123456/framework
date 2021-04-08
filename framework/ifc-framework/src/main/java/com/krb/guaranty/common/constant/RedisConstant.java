package com.krb.guaranty.common.constant;

/**
 * @author owell
 * @date 2019/3/5 13:28
 */
public class RedisConstant {

    public static final String REDIS_PREFIX = "";

    public static final String LOCK_PREFIX = REDIS_PREFIX + "LOCK_";

    public static final long DEFAULT_CACHE_EXPIRE_ONE_MINUTE = 60*1000;

    public static final long DEFAULT_CACHE_EXPIRE_FIVE_MINUTE = 5*60*1000;

    public static final long DEFAULT_CACHE_EXPIRE_ONE_HOUR = 60*60*1000;

    public static final long DEFAULT_CACHE_EXPIRE_ONE_DAY = 24*60*60*1000;

}
