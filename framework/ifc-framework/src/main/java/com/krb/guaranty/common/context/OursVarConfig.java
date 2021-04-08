package com.krb.guaranty.common.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author owell
 * @date 2018/8/22 14:50
 */
@Component
public class OursVarConfig {

    public static String springActive;

    public static String applicationName;

    public static String getSpringActive() {
        return springActive;
    }

    @Value("${spring.profiles.active:test}")
    public void setSpringActive(String springActive) {
        OursVarConfig.springActive = springActive;
    }

    public static String getApplicationName() {
        return applicationName;
    }

    @Value("${spring.application.name:}")
    public void setApplicationName(String applicationName) {
        OursVarConfig.applicationName = applicationName;
    }
}