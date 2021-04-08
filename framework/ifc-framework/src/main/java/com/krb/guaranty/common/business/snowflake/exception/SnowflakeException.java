package com.krb.guaranty.common.business.snowflake.exception;

import com.krb.guaranty.common.framework.exception.GuarantyException;

public class SnowflakeException extends GuarantyException {
    public SnowflakeException(String message) {
        super(message);
    }

    public SnowflakeException(Throwable cause) {
        super(cause);
    }
}
