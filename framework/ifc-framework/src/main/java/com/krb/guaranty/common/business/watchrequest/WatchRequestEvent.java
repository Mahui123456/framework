package com.krb.guaranty.common.business.watchrequest;

import com.krb.guaranty.common.framework.spring.event.OursEvent;
import lombok.Data;
import org.slf4j.MDC;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author owell
 * @date 2019/6/26 15:18
 */
@Data
public class WatchRequestEvent extends OursEvent {

    private String type;

    private String method;

    private String url;

    private String queryString;

    private Map<String,String[]> parameterMap;

    private Map<String,String> headers;

    private Object requestBody;

    private Object responseBody;

    private Object[] methodArgs;

    private Map<String,Object> extAttr;

    private String trace;

    private String span;

    public WatchRequestEvent(Object source) {
        super(source);
        this.setTrace(MDC.get("traceId"));
        this.setSpan(MDC.get("spanId"));
    }

    public WatchRequestEvent(Object source, String type, String method, String url, String queryString, Map<String, String[]> parameterMap, Object requestBody, Object[] methodArgs) {
        this(source);
        this.type = type;
        this.method = method;
        this.url = url;
        this.queryString = queryString;
        this.parameterMap = parameterMap;
        this.requestBody = requestBody;
        this.methodArgs = methodArgs;
    }

    public WatchRequestEvent(Object source, String type, String method, String url, String queryString, Map<String, String[]> parameterMap, Object requestBody, Object responseBody, Object[] methodArgs) {
        this(source);
        this.type = type;
        this.method = method;
        this.url = url;
        this.queryString = queryString;
        this.parameterMap = parameterMap;
        this.requestBody = requestBody;
        this.responseBody = responseBody;
        this.methodArgs = methodArgs;
    }

    public void putAttr(String key,Object value){
        if(extAttr == null){
            extAttr = new LinkedHashMap<>();
        }
        extAttr.put(key,value);
    }
}
