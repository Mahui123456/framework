package com.krb.guaranty.common.framework.mvc.entity;

import java.io.Serializable;
import java.util.Date;

public abstract class BaseDomain<ID extends Serializable> extends GenericDomain{

    public abstract ID getId();

    public abstract void setId(ID id);

    public abstract Date getCreateTime();

    public abstract void setCreateTime(Date createTime);

    public abstract Date getUpdateTime();

    public abstract void setUpdateTime(Date updateTime);

    @Override
    public Serializable findId() {
        return this.getId();
    }

    @Override
    public Date findCreateTime() {
        return this.getCreateTime();
    }

    @Override
    public Date findUpdateTime() {
        return this.getUpdateTime();
    }
}
