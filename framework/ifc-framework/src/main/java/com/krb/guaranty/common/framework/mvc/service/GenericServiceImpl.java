package com.krb.guaranty.common.framework.mvc.service;

import com.krb.guaranty.common.business.snowflake.annotations.AutoSnowflakeId;
import com.krb.guaranty.common.business.snowflake.exception.SnowflakeIdGenerateClassCastException;
import com.krb.guaranty.common.business.snowflake.service.SnowflakeIdService;
import com.krb.guaranty.common.framework.mvc.dao.BaseRepository;
import com.krb.guaranty.common.framework.mvc.entity.BaseDomain;
import com.krb.guaranty.common.framework.mvc.entity.GenericDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 最基础的通用类
 *
 * @param <T> 实体类
 */
public class GenericServiceImpl<M extends BaseRepository<T>, T extends GenericDomain> implements GenericService<T> {

    @Autowired
    protected M baseRepository;

    @Override
    public M getRepository() {
        return baseRepository;
    }

    /**
     * 是否自动雪花生成id
     *
     * !!!只对save方法做了特殊处理
     * 不支持saveOrUpdate,saveOrUpdateBatch
     *
     */
    protected boolean autoSnowflakeId = false;

    @Autowired
    protected SnowflakeIdService snowflakeIdService;

    public SnowflakeIdService getSnowflakeIdService() {
        return snowflakeIdService;
    }

    public GenericServiceImpl() {
        this.autoSnowflakeId = this.getClass().getAnnotation(AutoSnowflakeId.class)!=null;
    }

    @Override
    public boolean save(T entity) {
        setSnowflakeIdIfNull(entity);
        return retBool(baseRepository.insertEntity(entity));
    }

    protected void setSnowflakeIdIfNull(T entity) {
        if(autoSnowflakeId && entity.findId()==null && entity instanceof BaseDomain){
            try{
                ((BaseDomain) entity).setId(snowflakeIdService.nextId());
            }catch (ClassCastException e){
                throw new SnowflakeIdGenerateClassCastException("自动生成AutoSnowflakeId,"+entity.getClass().getName()+":id属性必须是Long类型",e);
            }
        }
    }

    @Override
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        List<T> saveList = new ArrayList<>(1000);
        for (T t : entityList) {
            setSnowflakeIdIfNull(t);

            saveList.add(t);
            if(saveList.size()>=batchSize){
                try{
                    baseRepository.insertEntity(saveList);
                }finally {
                    saveList = new ArrayList<>();
                }
            }
        }

        if(!CollectionUtils.isEmpty(saveList)){
            baseRepository.insertEntity(saveList);
        }
        return true;
    }

    @Override
    public boolean removeById(Serializable id) {
        return retBool(baseRepository.deleteWithId(id));
    }

    @Override
    public boolean removeByMap(Map<String, Object> columnMap) {
        return retBool(baseRepository.deleteByMap(columnMap));
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        return retBool(baseRepository.deleteBatchIds(idList));
    }

    @Override
    public boolean updateById(T entity) {
        return retBool(baseRepository.updateById(entity));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrUpdate(T entity) {
        /**
         * 目前基类的saveOrUpdate 只有mongo需要用,BaseServiceImpl是mybatis处理了snowflakeid的方法
         */
        setSnowflakeIdIfNull(entity);
        return retBool(baseRepository.insertOrUpdate(entity));
    }

    @Override
    public T getById(Serializable id) {
        return baseRepository.selectById(id);
    }

    @Override
    public List<T> listByIds(Collection<? extends Serializable> idList) {
        return baseRepository.selectBatchIds(idList);
    }

    @Override
    public List<T> listByMap(Map<String, Object> columnMap) {
        return baseRepository.selectByMap(columnMap);
    }

}