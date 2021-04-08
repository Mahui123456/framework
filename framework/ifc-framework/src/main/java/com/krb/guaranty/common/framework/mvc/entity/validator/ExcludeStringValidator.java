package com.krb.guaranty.common.framework.mvc.entity.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExcludeStringValidator implements ConstraintValidator<ExcludeString, String> {
    private String[] strs = null;

    public ExcludeStringValidator() {
    }

    public void initialize(ExcludeString excludeStrsAnnotation) {
        this.strs = excludeStrsAnnotation.strs();
    }

    public boolean isValid(String text, ConstraintValidatorContext constraintValidatorContext) {
        if (text != null && text.length() != 0 && this.strs != null && this.strs.length != 0) {
            String[] var3 = this.strs;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String str = var3[var5];
                if (text.contains(str)) {
                    String message = constraintValidatorContext.getDefaultConstraintMessageTemplate();
                    if (message == null || message.equals("")) {
                        constraintValidatorContext.buildConstraintViolationWithTemplate("不能包含特殊字符:" + str).addConstraintViolation();
                    }

                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }
}