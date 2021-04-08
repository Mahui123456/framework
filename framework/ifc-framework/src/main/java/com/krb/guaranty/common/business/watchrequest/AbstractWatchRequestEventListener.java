package com.krb.guaranty.common.business.watchrequest;

import com.krb.guaranty.common.framework.spring.event.OursEventListener;

/**
 * @author owell
 * @date 2019/6/26 15:52
 */
public abstract class AbstractWatchRequestEventListener<E extends WatchRequestEvent> extends OursEventListener<E> {
    @Override
    public void doEvent(E event) throws Exception {
        if(match(event.getType(),event)){
            doWatchRequestEvent(event);
        }
    }

    public boolean match(String type, E event){
        return true;
    }

    public abstract void doWatchRequestEvent(E event) throws Exception;
}
