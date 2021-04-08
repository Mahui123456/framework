package com.krb.guaranty.common.framework.spring.event;

import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * spring事件基础类,禁止直接使用
 */
public abstract class OursEvent extends ApplicationEvent {
    private Map<String, Object> threadVar;
    private boolean sync = false;

    public OursEvent(Object source) {
        super(source);
    }

    public Map<String, Object> getThreadVar() {
        return this.threadVar;
    }

    public void setThreadVar(Map<String, Object> threadVar) {
        this.threadVar = threadVar;
    }

    public boolean isSync() {
        return this.sync;
    }

    public void sync() {
        this.sync = true;
    }
}
