package com.krb.guaranty.common.framework.mvc.entity.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(
    validatedBy = {ExcludeStringValidator.class}
)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcludeString {
    String message() default "";

    String[] strs();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}