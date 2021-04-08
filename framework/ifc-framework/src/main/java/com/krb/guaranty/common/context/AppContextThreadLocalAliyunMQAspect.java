package com.krb.guaranty.common.context;

import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/**
 * 阿里云mq自动清理线程变量
 */
@Slf4j
@ConditionalOnClass(value = {MessageListener.class,MessageOrderListener.class})
@Aspect
@Component
public class AppContextThreadLocalAliyunMQAspect {

	@Around("execution (* com.aliyun.openservices.ons.api.MessageListener.consume(..))")
	public Object traceMessageListenerConsume(final ProceedingJoinPoint pjp) throws Throwable {
		try{
			return pjp.proceed();
		}finally {
			AppContext.clearThreadContext();
		}
	}

	@Around("execution (* com.aliyun.openservices.ons.api.order.MessageOrderListener.consume(..))")
	public Object traceOrderMessageListenerConsume(final ProceedingJoinPoint pjp) throws Throwable {
		try{
			return pjp.proceed();
		}finally {
			AppContext.clearThreadContext();
		}
	}

}