package com.krb.guaranty.common.framework.mvc.entity.validator;

import com.krb.guaranty.common.framework.mvc.enums.OursBaseEnum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * 注意!!!有值才校验 null不校验
 * 如果需要校验 非null 用 @NotNull
 */
public class EnumedDigitsValidator implements ConstraintValidator<EnumDigitValues, Integer> {
    private Set<Integer> values = null;

    public EnumedDigitsValidator() {
    }

    public void initialize(EnumDigitValues constraintAnnotation) {
        this.values = new HashSet();
        int[] arrayValues = constraintAnnotation.values();
        int[] var3 = arrayValues;
        int var4 = arrayValues.length;

        int valueInArray;
        for(int var5 = 0; var5 < var4; ++var5) {
            valueInArray = var3[var5];
            this.values.add(valueInArray);
        }

        Class<? extends OursBaseEnum> enumedValueClass = constraintAnnotation.enumClass();
        if (enumedValueClass.isEnum() && enumedValueClass != OursBaseEnum.class) {
            OursBaseEnum[] enumValues = (OursBaseEnum[])enumedValueClass.getEnumConstants();
            OursBaseEnum[] var11 = enumValues;
            valueInArray = enumValues.length;

            for(int var7 = 0; var7 < valueInArray; ++var7) {
                OursBaseEnum enumValue = var11[var7];
                this.values.add(enumValue.getCode());
            }
        }

    }

    public boolean isValid(Integer digit, ConstraintValidatorContext constraintValidatorContext) {
        return digit == null ? true : this.values.contains(digit);
    }
}