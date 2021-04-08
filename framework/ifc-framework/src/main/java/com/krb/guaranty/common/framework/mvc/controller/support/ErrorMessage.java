package com.krb.guaranty.common.framework.mvc.controller.support;

import org.slf4j.MDC;

/**
 * 做日志追踪用的
 * @author owell
 * @date 2018/5/25 12:30
 */
public class ErrorMessage extends Message<String>{

    private String trace;
    private String span;

    public ErrorMessage() {
        super();
        this.trace = MDC.get("traceId");
        this.span = MDC.get("spanId");
    }

    public ErrorMessage(Message<String> message) {
        this();

        this.resultCode = message.getResultCode();
        this.errMsg = message.getErrMsg();
        this.content = message.getContent();
        this.timestamp = message.getTimestamp();
    }

    public String getTrace() {
        return trace;
    }

    public void setTrace(String trace) {
        this.trace = trace;
    }

    public String getSpan() {
        return span;
    }

    public void setSpan(String span) {
        this.span = span;
    }

    @Override
    public String toString() {
        return "ErrorMessage{" +
                "trace='" + trace + '\'' +
                ", span='" + span + '\'' +
                ", resultCode=" + resultCode +
                ", errMsg='" + errMsg + '\'' +
                ", content=" + content +
                ", timestamp=" + timestamp +
                '}';
    }
}