package com.krb.guaranty.common.framework.exception;

/**
 * 字段校验不通过异常
 * @author owell
 * @date 2019/4/8 16:12
 */
public class ValidException extends GuarantyException{
    public ValidException() {
        super();
    }

    public ValidException(String message) {
        super(message);
    }

    public ValidException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidException(Throwable cause) {
        super(cause);
    }

    public ValidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}