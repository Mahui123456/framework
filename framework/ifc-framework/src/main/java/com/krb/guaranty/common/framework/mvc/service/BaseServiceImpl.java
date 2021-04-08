package com.krb.guaranty.common.framework.mvc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.krb.guaranty.common.framework.mvc.dao.BaseRepository4RDB;
import com.krb.guaranty.common.framework.mvc.entity.GenericDomain;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BaseServiceImpl<M extends BaseRepository4RDB<T>, T extends GenericDomain> extends GenericServiceImpl<M, T> implements BaseService<T> {

    public T getOne(Wrapper<T> queryWrapper, boolean throwEx) {
        Page page = new Page(1, 1);
        page.setSearchCount(false);
        List<T> list = this.page(page, queryWrapper).getRecords();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 批量操作 SqlSession
     */
    public SqlSession sqlSessionBatch() {
        return SqlHelper.sqlSessionBatch(currentModelClass());
    }

    /**
     * 释放sqlSession
     *
     * @param sqlSession session
     */
    public void closeSqlSession(SqlSession sqlSession) {
        SqlSessionUtils.closeSqlSession(sqlSession, GlobalConfigUtils.currentSessionFactory(currentModelClass()));
    }

    /**
     * 获取 SqlStatement
     *
     * @param sqlMethod ignore
     * @return ignore
     */
    public String sqlStatement(SqlMethod sqlMethod) {
        return SqlHelper.table(currentModelClass()).getSqlStatement(sqlMethod.getMethod());
    }


    public boolean save(T entity) {
        return super.save(entity);
    }

    /**
     * 批量插入
     *
     * @param entityList ignore
     * @param batchSize  ignore
     * @return ignore
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        String sqlStatement = sqlStatement(SqlMethod.INSERT_ONE);
        try (SqlSession batchSqlSession = sqlSessionBatch()) {
            int i = 0;
            for (T anEntityList : entityList) {
                setSnowflakeIdIfNull(anEntityList);

                batchSqlSession.insert(sqlStatement, anEntityList);
                if (i >= 1 && i % batchSize == 0) {
                    batchSqlSession.flushStatements();
                }
                i++;
            }
            batchSqlSession.flushStatements();
        }
        return true;
    }

    /**
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param entity 实体对象
     * @return boolean
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(T entity) {
        if (null != entity) {
            Class<?> cls = entity.getClass();
            TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
            Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
            String keyProperty = tableInfo.getKeyProperty();
            Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
            Object idVal = ReflectionKit.getMethodValue(cls, entity, tableInfo.getKeyProperty());
            return StringUtils.checkValNull(idVal) || Objects.isNull(getById((Serializable) idVal)) ? save(entity) : updateById(entity);
        }
        return false;
    }

    public boolean saveOrUpdateBatch(Collection<T> entityList) {
        return saveOrUpdateBatch(entityList, 1000);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        Assert.notEmpty(entityList, "error: entityList must not be empty");
        Class<?> cls = currentModelClass();
        TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
        try (SqlSession batchSqlSession = sqlSessionBatch()) {
            int i = 0;
            for (T entity : entityList) {
                Object idVal = ReflectionKit.getMethodValue(cls, entity, keyProperty);
                if (StringUtils.checkValNull(idVal) || Objects.isNull(getById((Serializable) idVal))) {
                    setSnowflakeIdIfNull(entity);
                    batchSqlSession.insert(sqlStatement(SqlMethod.INSERT_ONE), entity);
                } else {
                    MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
                    param.put(Constants.ENTITY, entity);
                    batchSqlSession.update(sqlStatement(SqlMethod.UPDATE_BY_ID), param);
                }
                // 不知道以后会不会有人说更新失败了还要执行插入 😂😂😂
                if (i >= 1 && i % batchSize == 0) {
                    batchSqlSession.flushStatements();
                }
                i++;
            }
            batchSqlSession.flushStatements();
        }
        return true;
    }

    public boolean updateBatchById(Collection<T> entityList) {
        return updateBatchById(entityList, 1000);
    }


    public boolean removeById(Serializable id) {
        return SqlHelper.delBool(getMapper().deleteById(id));
    }


    public boolean removeByMap(Map<String, Object> columnMap) {
        Assert.notEmpty(columnMap, "error: columnMap must not be empty");
        return SqlHelper.delBool(getMapper().deleteByMap(columnMap));
    }


    public boolean remove(Wrapper<T> wrapper) {
        return SqlHelper.delBool(getMapper().delete(wrapper));
    }


    public boolean removeByIds(Collection<? extends Serializable> idList) {
        return SqlHelper.delBool(getMapper().deleteBatchIds(idList));
    }


    public boolean updateById(T entity) {
        return retBool(getMapper().updateById(entity));
    }


    public boolean update(T entity, Wrapper<T> updateWrapper) {
        return retBool(getMapper().update(entity, updateWrapper));
    }

    public boolean update(Wrapper<T> updateWrapper) {
        return update(null, updateWrapper);
    }

    public T getOne(Wrapper<T> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        Assert.notEmpty(entityList, "error: entityList must not be empty");
        String sqlStatement = sqlStatement(SqlMethod.UPDATE_BY_ID);
        try (SqlSession batchSqlSession = sqlSessionBatch()) {
            int i = 0;
            for (T anEntityList : entityList) {
                MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
                param.put(Constants.ENTITY, anEntityList);
                batchSqlSession.update(sqlStatement, param);
                if (i >= 1 && i % batchSize == 0) {
                    batchSqlSession.flushStatements();
                }
                i++;
            }
            batchSqlSession.flushStatements();
        }
        return true;
    }


    public T getById(Serializable id) {
        return getMapper().selectById(id);
    }


    public List<T> listByIds(Collection<? extends Serializable> idList) {
        return getMapper().selectBatchIds(idList);
    }


    public List<T> listByMap(Map<String, Object> columnMap) {
        return getMapper().selectByMap(columnMap);
    }


    public Map<String, Object> getMap(Wrapper<T> queryWrapper) {
        return SqlHelper.getObject(getMapper().selectMaps(queryWrapper));
    }

    public <V> V getObj(Wrapper<T> queryWrapper, Function<? super Object, V> mapper) {
        return SqlHelper.getObject(listObjs(queryWrapper, mapper));
    }

    public int count(Wrapper<T> queryWrapper) {
        return SqlHelper.retCount(getMapper().selectCount(queryWrapper));
    }

    public int count() {
        return count(Wrappers.emptyWrapper());
    }

    public List<T> list(Wrapper<T> queryWrapper) {
        return getMapper().selectList(queryWrapper);
    }

    public List<T> list() {
        return list(Wrappers.emptyWrapper());
    }

    public IPage<T> page(IPage<T> page, Wrapper<T> queryWrapper) {
        mergePageToWrapper(page, queryWrapper);
        return getMapper().selectPage(page, queryWrapper);
    }

    public IPage<T> page(IPage<T> page) {
        return page(page, Wrappers.emptyWrapper());
    }

    public List<Map<String, Object>> listMaps(Wrapper<T> queryWrapper) {
        return getMapper().selectMaps(queryWrapper);
    }


    public List<Map<String, Object>> listMaps() {
        return listMaps(Wrappers.emptyWrapper());
    }

    public List<Object> listObjs() {
        return listObjs(Function.identity());
    }

    public <V> List<V> listObjs(Function<? super Object, V> mapper) {
        return listObjs(Wrappers.emptyWrapper(), mapper);
    }

    public List<Object> listObjs(Wrapper<T> queryWrapper) {
        return listObjs(queryWrapper, Function.identity());
    }

    public <V> List<V> listObjs(Wrapper<T> queryWrapper, Function<? super Object, V> mapper) {
        return getMapper().selectObjs(queryWrapper).stream().filter(Objects::nonNull).map(mapper).collect(Collectors.toList());
    }

    public IPage<Map<String, Object>> pageMaps(IPage<T> page, Wrapper<T> queryWrapper) {
        mergePageToWrapper(page, queryWrapper);
        return getMapper().selectMapsPage(page, queryWrapper);
    }

    public IPage<Map<String, Object>> pageMaps(IPage<T> page) {
        return pageMaps(page, Wrappers.emptyWrapper());
    }


    public void mergePageToWrapper(IPage<T> page, Wrapper<T> queryWrapper) {
		/*if(queryWrapper instanceof AbstractWrapper){
			//TODO 对condition的封装
			AbstractWrapper wrapper = ((AbstractWrapper) queryWrapper);
			//asc desc 的封装在拦截器里实现参考 PaginationInterceptor
			page.condition();
		}*/
    }
}