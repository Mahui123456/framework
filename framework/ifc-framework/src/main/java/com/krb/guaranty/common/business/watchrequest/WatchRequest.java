package com.krb.guaranty.common.business.watchrequest;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author owell
 * @date 2019/6/26 15:13
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
@Component
public @interface WatchRequest {
    String type();

    //监听到请求后抛出的事件
    Class<? extends WatchRequestEvent> event() default WatchRequestEvent.class;

    //是否记录请求返回值(接受到请求就抛出事件还是执行完成之后再抛出事件)
    boolean writeResponseBody() default false;

    //需要记录的请求头信息
    String[] writeHeaders() default {};
}
