package com.krb.guaranty.common.extention.sleuth;

import com.krb.guaranty.common.business.asyncbatchpersistence.service.AsyncBatchPersistenceService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/**
 * 批量定时任务执行自动加入sleuth调用链
 *
 * @Scheduled {@link org.springframework.scheduling.annotation.Scheduled} sleuth拦截方式见 TraceSchedulingAspect {@link}
 */
@Slf4j
@ConditionalOnClass(AsyncBatchPersistenceService.class)
@Aspect
@Component
public class TraceAsyncBatchPersistenceAspect {

	@Around("execution (* com.krb.guaranty.common.business.asyncbatchpersistence.service.AsyncBatchPersistenceService.runBatchPersistence(..))")
	public Object traceAsyncBatchPersistence(final ProceedingJoinPoint pjp) throws Throwable {
		return IFCSleuthUtil.newStart(pjp);
	}

}