package com.krb.guaranty.common.framework.mvc.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseDomain4MDB<ID extends Serializable> extends BaseDomain<ID> {

    @Id
    protected ID id;

    @CreatedDate
    //原因参考 RedisCacheGuarantyConfig
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date createTime;

    @LastModifiedDate
    //原因参考 RedisCacheGuarantyConfig
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date updateTime;

}
