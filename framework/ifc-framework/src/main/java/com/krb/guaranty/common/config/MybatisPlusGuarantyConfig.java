package com.krb.guaranty.common.config;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import com.krb.guaranty.common.extention.mybatisplus.handler.MyMetaObjectHandler;
import com.krb.guaranty.common.extention.mybatisplus.plugins.IFCPerformanceInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@ConditionalOnClass(value = {MybatisPlusAutoConfiguration.class})
@Configuration
public class MybatisPlusGuarantyConfig {

    /**
     * plus 的性能优化
     *
     * @return
     */
    @ConditionalOnMissingBean(value = {IFCPerformanceInterceptor.class,PerformanceInterceptor.class})
    @Bean
    public IFCPerformanceInterceptor performanceInterceptor() {
        IFCPerformanceInterceptor performanceInterceptor = new IFCPerformanceInterceptor();
        /* <!-- SQL 执行性能分析，开发环境使用，线上不推荐。 maxTime 指的是 sql 最大执行时长 --> */
        performanceInterceptor.setMaxTime(1000);
        /* <!--SQL是否格式化 默认false--> */
        performanceInterceptor.setWriteInLog(true);
        return performanceInterceptor;
    }

    /**
     * mybatis-plus分页插件
     */
    @ConditionalOnMissingBean
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor page = new PaginationInterceptor();
        return page;
    }

    @ConditionalOnMissingBean
    @Bean
    public MetaObjectHandler myMetaObjectHandler(){
        return new MyMetaObjectHandler();
    }
}