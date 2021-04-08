package com.krb.guaranty.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.krb.guaranty.common.business.snowflake.service.ConfigWorkIdProvider;
import com.krb.guaranty.common.business.snowflake.service.WorkIdProvider;

/**
 * 雪花id生成器
 */
@Configuration
public class SnowflakeGuarantyConfig {

	@ConditionalOnMissingBean
	@Bean
    public WorkIdProvider workIdProvider(){
        return new ConfigWorkIdProvider();
    }

}
