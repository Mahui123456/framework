package com.krb.guaranty.common.business.snowflake.annotations;


import java.lang.annotation.*;

/**
 * 自动生成雪花id
 * 注解需要标注在 Service 类头上
 * !!!注意相关的Domain主键类型必须是Long类型才行
 * !!!只对save,saveBatch方法做了特殊处理
 * 不支持saveOrUpdate,saveOrUpdateBatch
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface AutoSnowflakeId {
}