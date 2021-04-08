package com.krb.guaranty.common.business.threadlocalcache;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author owell
 * @date 2020/7/23 10:07
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Cacheable
public @interface ThreadLocalCacheable {

    @AliasFor(annotation = Cacheable.class)
    String[] cacheNames() default {"threadLocalCache"};

    @AliasFor(annotation = Cacheable.class)
    String key() default "";

    @AliasFor(annotation = Cacheable.class)
    String cacheManager() default "threadLocalCacheManager";

    @AliasFor(annotation = Cacheable.class)
    String condition() default "";

    @AliasFor(annotation = Cacheable.class)
    boolean sync() default false;

}
