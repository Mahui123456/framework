package com.krb.guaranty.common.config;

import com.krb.guaranty.common.business.threadlocalcache.ThreadLocalCacheManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author owell
 * @date 2020/7/23 10:07
 */
@Configuration
public class ThreadLocalCacheGuarantyConfig {

    @ConditionalOnMissingBean(value = {ThreadLocalCacheManager.class})
    @Bean(name = "threadLocalCacheManager")
    public ThreadLocalCacheManager threadLocalCacheManager(){
        return new ThreadLocalCacheManager();
    }

}
