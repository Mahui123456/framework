package com.krb.guaranty.common.business.sign;

import com.krb.guaranty.common.framework.exception.ValidException;

/**
 * @author owell
 * @date 2020/6/24 13:39
 */
public class IFCSignException extends ValidException {

    public IFCSignException() {
        super();
    }

    public IFCSignException(String message) {
        super(message);
    }

    public IFCSignException(String message, Throwable cause) {
        super(message, cause);
    }

    public IFCSignException(Throwable cause) {
        super(cause);
    }

    public IFCSignException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
