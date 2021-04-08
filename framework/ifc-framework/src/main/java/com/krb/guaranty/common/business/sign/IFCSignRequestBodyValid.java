package com.krb.guaranty.common.business.sign;

import java.lang.annotation.*;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IFCSignRequestBodyValid {
    /**
     * 如果指定了以注解指定的app
     * 如果未指定则从header中获取app
     * @return
     */
    String app() default "";

    /**
     * 如果指定了以注解指定的key作为鉴权的key
     * 如果未指定则从配置文件中获取app对应的key
     * @return
     */
    String key() default "";
}