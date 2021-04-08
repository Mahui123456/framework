package com.krb.guaranty.common.business.asyncbatchpersistence.configuration;

import com.krb.guaranty.common.business.asyncbatchpersistence.annotations.EnableAsyncBatchPersistence;
import com.krb.guaranty.common.business.asyncbatchpersistence.service.AsyncBatchPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author owell
 * @date 2019/8/19 16:11
 * @see EnableAsyncBatchPersistence
 */
public class AsyncBatchPersistenceAutoConfig implements SchedulingConfigurer {

    @Autowired(required = false)
    private List<AsyncBatchPersistenceService> asyncBatchPersistenceServices = new ArrayList<>();

    private static boolean batchPersistence = false;

    public static boolean isBatchPersistence() {
        return batchPersistence;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        batchPersistence = true;
        if(!CollectionUtils.isEmpty(asyncBatchPersistenceServices)){
            for (AsyncBatchPersistenceService service : asyncBatchPersistenceServices) {
                AsyncBathPersistenceTaskExecutor task = new AsyncBathPersistenceTaskExecutor(service);
                taskRegistrar.addTriggerTask(task, (triggerContext)->new CronTrigger(service.getBatchPersistenceCron()).nextExecutionTime(triggerContext));
            }
        }
    }


    public class AsyncBathPersistenceTaskExecutor implements Runnable{

        private AsyncBatchPersistenceService service;

        public AsyncBathPersistenceTaskExecutor() {
        }

        public AsyncBathPersistenceTaskExecutor(AsyncBatchPersistenceService service) {
            this.service = service;
        }

        @Override
        public void run() {
            service.runBatchPersistence();
        }
    }
}
