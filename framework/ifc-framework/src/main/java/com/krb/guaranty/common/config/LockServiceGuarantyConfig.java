package com.krb.guaranty.common.config;

import com.krb.guaranty.common.business.lock.service.LockService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * redis锁配置
 */
@Configuration
public class LockServiceGuarantyConfig {

    @ConditionalOnClass(RedisTemplate.class)
    @ConditionalOnMissingBean
    @Bean
    public LockService lockService() {
        return new LockService();
    }

}
