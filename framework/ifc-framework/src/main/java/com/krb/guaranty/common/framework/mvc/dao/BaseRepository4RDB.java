package com.krb.guaranty.common.framework.mvc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.krb.guaranty.common.framework.mvc.entity.GenericDomain;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.Objects;

/**
 * @author owell
 * @date 2019/6/12 18:33
 */
public interface BaseRepository4RDB<T extends GenericDomain> extends BaseMapper<T>,BaseRepository<T> {
    @Override
    default int insertOrUpdate(T entity){
        if (StringUtils.checkValNull(entity.findId()) || Objects.isNull(this.selectById(entity.findId()))) {
            return this.insertEntity(entity);
        } else {
            return this.updateById(entity);
        }
    }

    @Override
    default int insertEntity(T entity){
        return this.insert(entity);
    }

    @Override
    int updateById(@Param(Constants.ENTITY) T entity);

    @Override
    default void insertEntity(Collection<T> entity){
        //批量插入在service层实现(BaseServiceImpl)
        throw new AbstractMethodError();
    }
}
