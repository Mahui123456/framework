package com.krb.guaranty.common.framework.mvc.dao;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.krb.guaranty.common.framework.mvc.entity.GenericDomain;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author owell
 * @date 2019/6/13 09:05
 */
public interface BaseRepository<T extends GenericDomain> {

    /**
     * 插入一条记录
     *
     * @param entity 实体对象
     */
    int insertEntity(T entity);

    /**
     * 插入多条记录
     *
     * @param entity 实体对象
     */
    void insertEntity(Collection<T> entity);

    int insertOrUpdate(T entity);

    /**
     * 根据 ID 删除
     *
     * @param id 主键ID
     */
    int deleteWithId(Serializable id);

    /**
     * 根据 columnMap 条件，删除记录
     *
     * @param columnMap 表字段 map 对象
     */
    int deleteByMap(@Param(Constants.COLUMN_MAP) Map<String, Object> columnMap);

    /**
     * 删除（根据ID 批量删除）
     *
     * @param idList 主键ID列表(不能为 null 以及 empty)
     */
    int deleteBatchIds(@Param(Constants.COLLECTION) Collection<? extends Serializable> idList);

    /**
     * 根据 ID 修改
     *
     * @param entity 实体对象
     */
    int updateById(@Param(Constants.ENTITY) T entity);

    /**
     * 根据 ID 查询
     *
     * @param id 主键ID
     */
    T selectById(Serializable id);

    /**
     * 查询（根据ID 批量查询）
     *
     * @param idList 主键ID列表(不能为 null 以及 empty)
     */
    List<T> selectBatchIds(@Param(Constants.COLLECTION) Collection<? extends Serializable> idList);

    /**
     * 查询（根据 columnMap 条件）
     *
     * @param columnMap 表字段 map 对象
     */
    List<T> selectByMap(@Param(Constants.COLUMN_MAP) Map<String, Object> columnMap);

}
