package com.krb.guaranty.common.config;

import com.krb.guaranty.common.framework.intereptor.OursInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

//去掉了controller 方法path param参数注入有问题
@EnableSpringDataWebSupport
public class BaseWebMvcConfig extends WebMvcConfigurerAdapter {

    @Bean
    public OursInterceptor localCoreInterceptor(){
        return new OursInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        //注册自定义拦截器，添加拦截路径和排除拦截路径
        registry.addInterceptor(localCoreInterceptor()).addPathPatterns("/**").excludePathPatterns("/webjars/**","/swagger*/**","/static/**","/public/**");
    }

    /**
     * 过滤静态资源
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        registry.addResourceHandler("/**").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

}