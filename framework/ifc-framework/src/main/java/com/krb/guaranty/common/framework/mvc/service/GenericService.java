package com.krb.guaranty.common.framework.mvc.service;

import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.krb.guaranty.common.business.snowflake.service.SnowflakeIdService;
import com.krb.guaranty.common.framework.mvc.dao.BaseRepository;
import com.krb.guaranty.common.framework.mvc.entity.GenericDomain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * 最基础的通用类
 *
 * @param <T> 实体类
 */
public interface GenericService<T extends GenericDomain> {

    public SnowflakeIdService getSnowflakeIdService();

    /**
     * 插入一条记录（选择字段，策略插入）
     *
     * @param entity 实体对象
     */
    boolean save(T entity);

    /**
     * 插入（批量）
     *
     * @param entityList 实体对象集合
     */
    default boolean saveBatch(Collection<T> entityList) {
        return saveBatch(entityList, 1000);
    }

    /**
     * 插入（批量）
     *
     * @param entityList 实体对象集合
     * @param batchSize  插入批次数量
     */
    boolean saveBatch(Collection<T> entityList, int batchSize);

    /**
     * 根据 ID 删除
     *
     * @param id 主键ID
     */
    boolean removeById(Serializable id);

    /**
     * 根据 columnMap 条件，删除记录
     *
     * @param columnMap 表字段 map 对象
     */
    boolean removeByMap(Map<String, Object> columnMap);

    /**
     * 删除（根据ID 批量删除）
     *
     * @param idList 主键ID列表
     */
    boolean removeByIds(Collection<? extends Serializable> idList);

    /**
     * 根据 ID 选择修改
     *
     * @param entity 实体对象
     */
    boolean updateById(T entity);

    /**
     * TableId 注解存在更新记录，否插入一条记录
     * 建议明确业务场景是插入,还是更新  不支持@AutoSnowflakeId
     *
     * @param entity 实体对象
     */
    @Deprecated
    boolean saveOrUpdate(T entity);

    /**
     * 根据 ID 查询
     *
     * @param id 主键ID
     */
    T getById(Serializable id);

    /**
     * 查询（根据ID 批量查询）
     *
     * @param idList 主键ID列表
     */
    Collection<T> listByIds(Collection<? extends Serializable> idList);

    /**
     * 查询（根据 columnMap 条件）
     *
     * @param columnMap 表字段 map 对象
     */
    Collection<T> listByMap(Map<String, Object> columnMap);


    /**
     * 判断数据库操作是否成功
     *
     * @param result 数据库操作返回影响条数
     * @return boolean
     */
    default boolean retBool(Integer result) {
        return SqlHelper.retBool(result);
    }

    default Class<T> currentModelClass() {
        return ReflectionKit.getSuperClassGenericType(getClass(), 1);
    }

    <R extends BaseRepository<T>> R getRepository();
}