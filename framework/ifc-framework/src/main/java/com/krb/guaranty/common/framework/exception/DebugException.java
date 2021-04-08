package com.krb.guaranty.common.framework.exception;

/**
 * 如果抛出的是DebugException 返回值会携带异常栈信息
 * @author owell
 * @date 2019/6/19 11:12
 */
public class DebugException extends GuarantyException {
    public DebugException() {
    }

    public DebugException(String message) {
        super(message);
    }

    public DebugException(String message, Throwable cause) {
        super(message, cause);
    }

    public DebugException(Throwable cause) {
        super(cause);
    }

    public DebugException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
