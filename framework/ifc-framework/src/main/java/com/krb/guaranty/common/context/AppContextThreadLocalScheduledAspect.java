package com.krb.guaranty.common.context;

import com.krb.guaranty.common.business.asyncbatchpersistence.service.AsyncBatchPersistenceService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/**
 * 定时任务自动清理线程变量
 */
@Slf4j
@ConditionalOnClass(AsyncBatchPersistenceService.class)
@Aspect
@Component
public class AppContextThreadLocalScheduledAspect {

	@Around("execution (* com.krb.guaranty.common.business.asyncbatchpersistence.service.AsyncBatchPersistenceService.runBatchPersistence(..))")
	public Object traceAsyncBatchPersistence(final ProceedingJoinPoint pjp) throws Throwable {
		try{
			return pjp.proceed();
		}finally {
			AppContext.clearThreadContext();
		}
	}


	@Around("execution (@org.springframework.scheduling.annotation.Scheduled  * *.*(..))")
	public Object traceBackgroundThread(final ProceedingJoinPoint pjp) throws Throwable {
		try{
			return pjp.proceed();
		}finally {
			AppContext.clearThreadContext();
		}
	}

}