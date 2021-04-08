package com.krb.guaranty.common.extention.sleuth;

import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/**
 * 阿里云mq执行自动加入sleuth调用链
 */
@Slf4j
@ConditionalOnClass(value = {MessageListener.class,MessageOrderListener.class})
@Aspect
@Component
public class TraceAliyunMQAspect {

	@Around("execution (* com.aliyun.openservices.ons.api.MessageListener.consume(..))")
	public Object traceMessageListenerConsume(final ProceedingJoinPoint pjp) throws Throwable {
		return IFCSleuthUtil.newStart(pjp);
	}

	@Around("execution (* com.aliyun.openservices.ons.api.order.MessageOrderListener.consume(..))")
	public Object traceOrderMessageListenerConsume(final ProceedingJoinPoint pjp) throws Throwable {
		return IFCSleuthUtil.newStart(pjp);
	}

}