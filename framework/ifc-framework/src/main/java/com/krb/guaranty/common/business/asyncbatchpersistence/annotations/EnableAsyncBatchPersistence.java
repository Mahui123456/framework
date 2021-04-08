package com.krb.guaranty.common.business.asyncbatchpersistence.annotations;


import com.krb.guaranty.common.business.asyncbatchpersistence.configuration.AsyncBatchPersistenceAutoConfig;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({AsyncBatchPersistenceAutoConfig.class})
@EnableScheduling
public @interface EnableAsyncBatchPersistence {
}