package com.krb.guaranty.common.framework.intereptor.advice;

import com.krb.guaranty.common.constant.AppConstant;
import com.krb.guaranty.common.context.AppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 调用invode 进入controller 是需要从requestbody中转换参数,可以处理参数转换前,后的逻辑
 * @author owell
 * @date 2018/5/24 18:44
 */
@ControllerAdvice
public class OursRequestBodyAdvice extends RequestBodyAdviceAdapter {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${ifc.request.show-body:true}")
    boolean requestShowBody;

    @Value("${ifc.request.show-json-body:false}")
    boolean requestShowJsonBody;

    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        return super.beforeBodyRead(inputMessage, parameter, targetType, converterType);
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        if(requestShowBody || requestShowJsonBody){
            AppContext.putThreadContext(AppConstant.REQUEST_BODY,body);
        }
        //log.info("Request Execute >>> convertBodyToParam:{}",JSON.toJSON(body));
        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
    }
}
