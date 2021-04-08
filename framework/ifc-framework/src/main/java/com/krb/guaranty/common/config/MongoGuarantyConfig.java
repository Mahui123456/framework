package com.krb.guaranty.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.Collections;

@EnableMongoAuditing
@Configuration
@ConditionalOnClass(value = {MongoTemplate.class})
@ConditionalOnProperty(name = "spring.data.mongodb.uri")
public class MongoGuarantyConfig {

    /**
     * MongoConfigurationSupport
     * 下面有三个abstract实现类 但是没有找到真正干活的实现类
     * @return
     */
    @ConditionalOnMissingBean
    @Bean
    public CustomConversions customConversions() {
        return new MongoCustomConversions(Collections.emptyList());
    }

    /**
     * 为了去掉mongo 保存的文档中的 _class 属性
     * @param factory
     * @param context
     * @param customConversions
     * @return
     */
    @ConditionalOnMissingBean
    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDbFactory factory, MongoMappingContext context, CustomConversions customConversions) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
        mappingConverter.setCustomConversions(customConversions);
        // Don't save _class to mongo
        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));

        //现在mongo存储的value不是a.b 是存成结构化的数据所以不需要转换
        //把存储的key中的"."转换成你要的字符
        //mappingConverter.setMapKeyDotReplacement("\\+");

        return mappingConverter;
    }

}