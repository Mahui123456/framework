package com.krb.guaranty.common.framework.mvc.entity;


import com.krb.guaranty.common.framework.exception.ValidException;
import com.krb.guaranty.common.framework.mvc.entity.validator.ValidatorProvider;

import java.io.Serializable;

public interface Validator extends Serializable {
    /**
     * 校验字段合法性
     * @throws ValidException
     */
    default void validateBean() throws ValidException {
        beforeValidBean();
        ValidatorProvider.validate(this, new Class[0]);
        afterValidBean();
    }

    default void beforeValidBean(){}

    default void afterValidBean(){}
}
