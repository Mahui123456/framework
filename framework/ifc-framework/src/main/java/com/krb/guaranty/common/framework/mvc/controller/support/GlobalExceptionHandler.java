package com.krb.guaranty.common.framework.mvc.controller.support;

import com.krb.guaranty.common.framework.exception.DebugException;
import com.krb.guaranty.common.framework.exception.TipsException;
import com.krb.guaranty.common.framework.exception.ValidException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 异常统一处理
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 服务器端错误统一处理
     *
     * @param e
     * @return
     * @throws Exception
     */
    @ResponseBody
    @ExceptionHandler
    public Message jsonErrorHandler(Throwable e, HttpServletResponse response, HttpServletRequest request){
        Message result = new ErrorMessage(Message.error(DefaultCode.SYS_ERROR,null));
        logger.error("未知异常:timestamp:["+result.getTimestamp()+"]", e);
        return result;
    }

    @ResponseBody
    @ExceptionHandler
    public Message httpMessageNotReadableException(HttpMessageNotReadableException httpMessageNotReadableException, HttpServletResponse response, HttpServletRequest request) {
        Message result = new ErrorMessage(Message.error(DefaultCode.ILLEGAL_ARGUMENT,null));
        logger.error("对象转换出错:timestamp:["+result.getTimestamp()+"]...", httpMessageNotReadableException);
        return result;
    }

    @ResponseBody
    @ExceptionHandler
    public Message handleValidateException(ValidException validateException, HttpServletResponse response, HttpServletRequest request) {
        Message result = new ErrorMessage(Message.error(DefaultCode.ILLEGAL_ARGUMENT,validateException.getMessage()));
        logger.error("程序字段校验出错:timestamp:["+result.getTimestamp()+"]...", validateException);
        return result;
    }

    @ResponseBody
    @ExceptionHandler
    public Message handleValidateException(TipsException tipsException, HttpServletResponse response, HttpServletRequest request) {
        Message result = new ErrorMessage(Message.error(DefaultCode.ILLEGAL_ARGUMENT,tipsException.getMessage()));
        logger.error("程序异常返回异常消息:timestamp:["+result.getTimestamp()+"]...", tipsException);
        return result;
    }

    @ResponseBody
    @ExceptionHandler
    public  Message handlerDebugException(DebugException debugException, HttpServletResponse response, HttpServletRequest request){
        Message result = new ErrorMessage(Message.error(DefaultCode.SYS_ERROR,ExceptionUtils.getStackTrace(debugException)));
        logger.error("未知调试异常:timestamp:["+result.getTimestamp()+"]", debugException);
        return result;
    }
}
