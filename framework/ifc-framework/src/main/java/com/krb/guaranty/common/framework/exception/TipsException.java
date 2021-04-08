package com.krb.guaranty.common.framework.exception;

/**
 * 异常信息返回到接口errMessage
 * @author owell
 * @date 2019/4/8 16:12
 */
public class TipsException extends GuarantyException{
    public TipsException() {
        super();
    }

    public TipsException(String message) {
        super(message);
    }

    public TipsException(String message, Throwable cause) {
        super(message, cause);
    }

    public TipsException(Throwable cause) {
        super(cause);
    }

    public TipsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}