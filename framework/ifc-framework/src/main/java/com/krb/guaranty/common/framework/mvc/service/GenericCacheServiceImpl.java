package com.krb.guaranty.common.framework.mvc.service;

import com.krb.guaranty.common.constant.RedisConstant;
import com.krb.guaranty.common.framework.mvc.dao.BaseRepository;
import com.krb.guaranty.common.framework.mvc.entity.GenericDomain;
import com.krb.guaranty.common.framework.mvc.utils.OursCoreAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class GenericCacheServiceImpl<M extends BaseRepository<T>, T extends GenericDomain> extends GenericServiceImpl<M, T> {

    @Autowired
    protected RedisTemplate redisTemplate;

    @Override
    public T getById(Serializable id) {
        String cacheKey = getCacheKey(id);
        T t = (T) redisTemplate.opsForValue().get(cacheKey);
        if (t == null) {
            t = super.getById(id);
            if (t != null) {
                redisTemplate.opsForValue().set(cacheKey, t, getExpireTime(), TimeUnit.MILLISECONDS);
            } else {
                //TODO 垃圾数据
            }
        }
        return t;
    }

    public T getById4DB(Serializable id) {
        return super.getById(id);
    }

    /**
     * 缓存获过期时间 单位毫秒,给子类重写扩展
     *
     * @return
     */
    public long getExpireTime() {
        return RedisConstant.DEFAULT_CACHE_EXPIRE_ONE_HOUR;
    }

    @Override
    public boolean removeById(Serializable id) {
        boolean result = super.removeById(id);
        clearCache(id);
        return result;
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        List<String> cacheKeys = new ArrayList<>(idList.size());
        for (Serializable id : idList) {
            cacheKeys.add(getCacheKey(id));
        }
        boolean result = super.removeByIds(idList);
        redisTemplate.delete(cacheKeys);
        return result;
    }

    @Override
    public boolean updateById(T entity) {
        boolean result = super.updateById(entity);
        clearCache(getId(entity));
        return result;
    }

    @Override
    public boolean saveOrUpdate(T entity) {
        boolean result = super.saveOrUpdate(entity);
        this.clearCache(getId(entity));
        return result;
    }

    public Boolean clearCache(Serializable id) {
        return redisTemplate.delete(getCacheKey(id));
    }

    public abstract String getCacheKey(Serializable id);

    public Serializable getId(T entity) {
        final Serializable id = entity.findId();
        OursCoreAssert.notNull(id, "使用缓存service 实体bean.findId()必须不能返回空值!!!");
        return id;
    }
}