package com.krb.guaranty.common.framework.mvc.entity.validator;

import com.krb.guaranty.common.framework.exception.ValidException;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

public class ValidatorProvider {
    private static Validator instance = null;

    public ValidatorProvider() {
    }

    public static Validator getValidator() {
        if (instance == null) {
            instance = ((HibernateValidatorConfiguration)Validation.byProvider(HibernateValidator.class).configure()).failFast(true).buildValidatorFactory().getValidator();
        }

        return instance;
    }

    public static void validate(Object object, Class... groups) throws ValidException {
        Set<ConstraintViolation<Object>> result = getValidator().validate(object, groups);
        if (result != null && !result.isEmpty()) {
            throw new ValidException(((ConstraintViolation)result.iterator().next()).getMessage());
        }
    }
}