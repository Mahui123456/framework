package com.krb.guaranty.common.framework.mvc.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

@Data
public abstract class BaseDomain4RDB extends BaseDomain<Long> {

    //@TableId(value = "id",type = IdType.AUTO)
    //protected Long id;

    @TableField(fill = FieldFill.INSERT)
    //原因参考 RedisCacheGuarantyConfig
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    //原因参考 RedisCacheGuarantyConfig
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date updateTime;

}
