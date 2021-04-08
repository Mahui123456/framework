package com.krb.guaranty.common.framework.mvc.entity.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 注意!!!有值才校验 null不校验
 * 如果需要校验 非null 用 @NotNull
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = {EnumValuesValidator.class}
)
public @interface EnumValues {

    Class<?> value();

    String message() default "只能是枚举的特定值";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
