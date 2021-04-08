package com.krb.guaranty.common.framework.mvc.entity.validator;

import com.krb.guaranty.common.framework.mvc.enums.OursBaseEnum;
import com.krb.guaranty.common.framework.mvc.enums.OursBaseStringEnum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * 注意!!!有值才校验 null不校验
 * 如果需要校验 非null 用 @NotNull
 */
public class EnumValuesValidator implements ConstraintValidator<EnumValues, Object> {

    /**
     * 枚举类型值的集合
     */
    private Set<Object> values = new HashSet<>();

    @Override
    public void initialize(EnumValues constraintAnnotation) {
        Class<?> targetEnum = constraintAnnotation.value();
        if(targetEnum.isEnum()){
            Object[] enumConstants = targetEnum.getEnumConstants();
            for (Object iEnum : enumConstants) {
                if(iEnum instanceof OursBaseEnum){
                    values.add(((OursBaseEnum) iEnum).getCode());
                }else if(iEnum instanceof OursBaseStringEnum){
                    values.add(((OursBaseStringEnum) iEnum).getValue());
                }else if(iEnum instanceof Enum){
                    values.add(((Enum)iEnum).name());
                }
            }
        }
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if(value == null){
            return true;
        }
        if(value instanceof OursBaseEnum){
            return this.values.contains(((OursBaseEnum) value).getCode());
        }else if(value instanceof OursBaseStringEnum){
            return this.values.contains(((OursBaseStringEnum) value).getValue());
        }else if(value instanceof Enum){
            return this.values.contains(((Enum) value).name());
        }
        return this.values.contains(value);
    }
}
