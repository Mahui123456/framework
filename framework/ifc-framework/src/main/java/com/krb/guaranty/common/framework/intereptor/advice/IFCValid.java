package com.krb.guaranty.common.framework.intereptor.advice;

import java.lang.annotation.*;

/**
 * 标注在controller 的参数上面
 * 参数需要同时标准@RequestBody
 * 会在RequestIFCAutoValidRequestBodyAdvice里处理业务逻辑
 * @author owell
 * @date 2020/6/10 11:15
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IFCValid {
}
