package com.krb.guaranty.common.framework.mvc.entity;

import java.io.Serializable;
import java.util.Date;

public abstract class GenericDomain implements Validator {

    /**
     * 获取当前bean的id字段
     * @return 可以为null
     *  1.如果为null则无法使用缓存service(GenericCacheServiceImpl,BaseCacheServiceImpl)
     *  2.如果为null则会影响BaseRepository4RDB.insertOrUpdate方法判断 即调用insertOrUpdate只会insert 不会 update
     */
    public Serializable findId(){
        return null;
    }

    public Date findCreateTime(){
        return null;
    }

    public Date findUpdateTime(){
        return null;
    }

}
