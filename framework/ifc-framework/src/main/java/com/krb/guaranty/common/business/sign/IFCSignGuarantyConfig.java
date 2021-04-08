package com.krb.guaranty.common.business.sign;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * redis锁配置
 */
@Configuration
@EnableConfigurationProperties({IFCSignRequestBodyProperties.class})
public class IFCSignGuarantyConfig {
}
