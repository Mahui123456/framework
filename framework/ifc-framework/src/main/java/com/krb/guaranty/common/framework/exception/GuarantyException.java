package com.krb.guaranty.common.framework.exception;

/**
 * @author owell
 * @date 2019/4/10 13:56
 */
public class GuarantyException extends RuntimeException{
    public GuarantyException() {
        super();
    }

    public GuarantyException(String message) {
        super(message);
    }

    public GuarantyException(String message, Throwable cause) {
        super(message, cause);
    }

    public GuarantyException(Throwable cause) {
        super(cause);
    }

    public GuarantyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
