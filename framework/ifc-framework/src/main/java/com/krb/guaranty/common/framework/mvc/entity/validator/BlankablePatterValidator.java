package com.krb.guaranty.common.framework.mvc.entity.validator;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author owell
 * @since 2020/10/20
 */
public class BlankablePatterValidator implements ConstraintValidator<BlankablePatter, String> {

    private BlankablePatter blankablePatter;

    @Override
    public void initialize(BlankablePatter constraintAnnotation) {
        this.blankablePatter = constraintAnnotation;
    }

    @Override
    public boolean isValid(String o, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isBlank(o)) {
            return true;
        }
        final String regexp = blankablePatter.regexp();
        if (StringUtils.isBlank(regexp)) {
            return true;
        }
        return o.matches(regexp);
    }
}
