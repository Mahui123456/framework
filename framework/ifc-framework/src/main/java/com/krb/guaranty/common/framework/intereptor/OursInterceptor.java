package com.krb.guaranty.common.framework.intereptor;

import com.krb.guaranty.common.constant.AppConstant;
import com.krb.guaranty.common.context.AppContext;
import com.krb.guaranty.common.utils.IFCJSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 通用的拦截器处理
 * 目前主要用于清理线程变量
 */
public class OursInterceptor extends HandlerInterceptorAdapter {
    /**
     * 日志对象
     */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1、开始时间
        Long beginTime = System.currentTimeMillis();

        //线程绑定变量
        AppContext.putThreadContext("request_start_time", beginTime);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        try {
            //得到线程绑定的局部变量（开始时间）
            Long beginTime = (Long) AppContext.getThreadContext("request_start_time");
            Long subTime = null;
            if (beginTime == null) {
                subTime = 0L;
            } else {
                subTime = System.currentTimeMillis() - beginTime;
            }

            log.info("Request Execute >>> URI={} >>> Method={} >>> time cost {}ms >>> Params=[{}] >>> RequestBody=[{}] >>> ReturnValue=[{}]",
                    request.getRequestURI(),
                    request.getMethod(),
                    subTime,
                    IFCJSONUtil.toJSONString(request.getParameterMap()),
                    getShowBody(AppConstant.REQUEST_BODY,requestShowBody,requestShowJsonBody),
                    getShowBody(AppConstant.RESPONSE_BODY,responseShowBody,responseShowJsonBody)
            );

            super.afterCompletion(request, response, handler, ex);
        }finally {
            //清理线程变量
            AppContext.clearThreadContext();
        }
    }

    @Value("${ifc.request.show-body:true}")
    boolean requestShowBody;

    @Value("${ifc.request.show-json-body:false}")
    boolean requestShowJsonBody;

    @Value("${ifc.response.show-body:true}")
    boolean responseShowBody;

    @Value("${ifc.response.show-json-body:false}")
    boolean responseShowJsonBody;

    public Object getShowBody(String key,boolean showBody,boolean showJsonBody){
        String result = null;
        Object body = AppContext.getThreadContext(key);
        if(body != null){
            if(showJsonBody){
                result = IFCJSONUtil.toJSONString(body);
            }else if(showBody){
                result = body.toString();
            }
        }

        //限制最多输出2000个字符
        result = result != null ? result.substring(0,Math.min(result.length(),2000)) : result;
        return result;
    }

}
