package com.krb.guaranty.common.framework.mvc.entity.validator;

import com.krb.guaranty.common.framework.mvc.enums.OursBaseStringEnum;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 注意!!!有值才校验 null和""空字符串不校验
 * 如果需要校验 非null 用 @NotNull
 * 如果需要校验 非空   用 @NotEmpty/@NotBlank
 */
@Constraint(
    validatedBy = {EnumStringValidator.class}
)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnumStringValues {
    String message() default "只能是枚举的特定值";

    String[] values() default {};

    Class<? extends OursBaseStringEnum> enumClass() default OursBaseStringEnum.class;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
