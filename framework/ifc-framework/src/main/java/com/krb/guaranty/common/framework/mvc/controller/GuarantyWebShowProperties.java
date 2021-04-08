package com.krb.guaranty.common.framework.mvc.controller;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Data
@ConfigurationProperties(prefix = "guaranty.web.show")
public class GuarantyWebShowProperties {
    private List<String> scanEnumPackages = new ArrayList<>();
}