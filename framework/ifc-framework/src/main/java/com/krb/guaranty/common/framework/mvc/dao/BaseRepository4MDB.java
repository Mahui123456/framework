package com.krb.guaranty.common.framework.mvc.dao;

import com.krb.guaranty.common.framework.mvc.entity.GenericDomain;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author owell
 * @date 2019/5/13 12:42
 */
@NoRepositoryBean
public interface BaseRepository4MDB<T extends GenericDomain> extends MongoRepository<T, Serializable>, BaseRepository<T> {
    @Override
    default int insertEntity(T entity) {
        this.save(entity);
        return 1;
    }

    @Override
    default void insertEntity(Collection<T> entity){
        this.insert(entity);
    }

    @Override
    default int insertOrUpdate(T entity) {
        this.save(entity);
        return 1;
    }

    @Override
    default int deleteWithId(Serializable id){
        this.deleteById(id);
        return 1;
    }

    @Override
    default int deleteByMap(Map<String, Object> columnMap) {
        throw new AbstractMethodError();
    }

    @Override
    default int deleteBatchIds(Collection<? extends Serializable> idList) {
        throw new AbstractMethodError();
    }

    @Override
    default int updateById(T entity) {
        this.save(entity);
        return 1;
    }

    @Override
    default T selectById(Serializable id) {
        return this.findById(id).orElse(null);
    }

    @Override
    default List<T> selectBatchIds(Collection<? extends Serializable> idList) {
        //FIXME
        throw new AbstractMethodError();
    }

    @Override
    default List<T> selectByMap(Map<String, Object> columnMap) {
        throw new AbstractMethodError();
    }
}
