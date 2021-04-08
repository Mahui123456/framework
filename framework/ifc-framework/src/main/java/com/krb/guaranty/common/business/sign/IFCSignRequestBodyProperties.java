package com.krb.guaranty.common.business.sign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author owell
 * @date 2020/6/24 12:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "guaranty.sign")
public class IFCSignRequestBodyProperties {

    /**
     * 是否跳过sign验证
     * true:跳过
     * 默认是false
     * 测试环境测试使用
     */
    private boolean skip = false;

    private List<IFCSignAppKey> keys;

}
