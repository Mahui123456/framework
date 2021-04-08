package com.krb.guaranty.common.framework.mvc.entity.validator;


import com.krb.guaranty.common.framework.mvc.enums.OursBaseEnum;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 注意!!!有值才校验 null不校验
 * 如果需要校验 非null 用 @NotNull
 *
 * 已废弃参考:
 * @see EnumValues
 */
@Deprecated
@Constraint(
    validatedBy = {EnumedDigitsValidator.class}
)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnumDigitValues {
    String message() default "只能是枚举的特定值";

    int[] values() default {};

    Class<? extends OursBaseEnum> enumClass() default OursBaseEnum.class;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
