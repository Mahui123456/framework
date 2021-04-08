package com.krb.guaranty.common.config;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashSet;

@ConditionalOnMissingBean(value = Docket.class)
@ConditionalOnClass(value = {Docket.class})
@Configuration
@EnableSwagger2
public class Swagger2GuarantyConfig {
    private final String DEFAULT_CONTENT_TYPE = "application/json";

    @Value("${spring.application.name:}")
    private String applicationName;

    @Value("${swagger.title:}")
    private String swaggerTitle;

    @Value("${swagger.description:}")
    private String swaggerDescription;

    @Value("${swagger.version:1.0.0}")
    private String swaggerVersion;

    @Value("${swagger.base-package:com.krb}")
    private String swaggerBasePackage;

    @Value("${swagger.paths:}")
    private String swaggerPaths;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(basePackage(swaggerBasePackage))
                .paths(swaggerPaths.equals("")?PathSelectors.any():PathSelectors.ant(swaggerPaths))
                .build()
                .consumes(consumesSet())
                ;
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact(applicationName, "", "");
        return new ApiInfoBuilder()
                .title(swaggerTitle.equals("")?applicationName:swaggerTitle)
                .description(swaggerDescription.equals("")?applicationName:swaggerDescription)
                .contact(contact)
                .version(swaggerVersion)
                .build();
    }

    private HashSet<String> consumesSet () {
        HashSet<String> consumes = new HashSet<String>();
        consumes.add(DEFAULT_CONTENT_TYPE);
        return consumes;
    }


    /**
     * 重写basePackage方法，使能够实现多包访问
     * @author  jinhaoxun
     * @date 2019/1/26
     * @param
     */
    public static Predicate<RequestHandler> basePackage(final String basePackage) {
        return input -> declaringClass(input).transform(handlerPackage(basePackage)).or(true);
    }

    // 定义分隔符
    private static final String splitor = ";";

    private static Function<Class<?>, Boolean> handlerPackage(final String basePackage)     {
        return input -> {
            // 循环判断匹配
            for (String strPackage : basePackage.split(splitor)) {
                boolean isMatch = input.getPackage().getName().startsWith(strPackage);
                if (isMatch) {
                    return true;
                }
            }
            return false;
        };
    }

    private static Optional<? extends Class<?>> declaringClass(RequestHandler input) {
        return Optional.fromNullable(input.declaringClass());
    }

}
