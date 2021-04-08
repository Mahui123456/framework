package com.krb.guaranty.common.framework.intereptor.advice;

import com.krb.guaranty.common.constant.AppConstant;
import com.krb.guaranty.common.context.AppContext;
import com.krb.guaranty.common.framework.mvc.controller.support.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class OursResponseBodyAdvice implements ResponseBodyAdvice {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${ifc.response.show-body:true}")
    boolean responseShowBody;

    @Value("${ifc.response.show-json-body:false}")
    boolean responseShowJsonBody;

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return returnType.getParameterType().isAssignableFrom(Message.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if(responseShowBody || responseShowJsonBody){
            AppContext.putThreadContext(AppConstant.RESPONSE_BODY,body);
        }
        //log.info("Response Execute >>> returnValue:{}",body);
        return body;
    }
}