package com.krb.guaranty.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ScheduledGuarantyConfig implements SchedulingConfigurer {

    @ConditionalOnMissingBean
    @Bean(destroyMethod="shutdown")
    public ExecutorService scheduledExecutorService(){
        return Executors.newScheduledThreadPool(8);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setScheduler(scheduledExecutorService());
    }
}