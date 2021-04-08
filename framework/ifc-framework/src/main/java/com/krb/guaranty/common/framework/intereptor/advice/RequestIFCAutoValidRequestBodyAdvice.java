package com.krb.guaranty.common.framework.intereptor.advice;

import com.krb.guaranty.common.framework.mvc.entity.Validator;
import com.krb.guaranty.common.framework.mvc.entity.validator.ValidatorProvider;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import javax.validation.Valid;
import java.lang.reflect.Type;

/**
 * 处理如果标注了IFCAutoValid 则执行validatebean方法
 * @author owell
 * @date 2020/6/10 11:14
 */
@ControllerAdvice
public class RequestIFCAutoValidRequestBodyAdvice extends RequestBodyAdviceAdapter {

    @Override
    public boolean supports(MethodParameter parameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return parameter.hasParameterAnnotation(IFCValid.class);
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        if(body != null){
            if(body instanceof Validator){
                ((Validator) body).validateBean();
            }else{
                if(!parameter.hasParameterAnnotation(Valid.class)){
                    ValidatorProvider.validate(body, new Class[0]);
                }
            }
        }
        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
    }

}
