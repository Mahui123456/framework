package com.krb.guaranty.common.framework.mvc.entity.validator;

import com.krb.guaranty.common.framework.mvc.enums.OursBaseStringEnum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * 注意!!!有值才校验 null和""空字符串不校验
 * 如果需要校验 非null 用 @NotNull
 * 如果需要校验 非空   用 @NotEmpty/@NotBlank
 *
 * 已废弃参考:
 * @see EnumValues
 */
@Deprecated
public class EnumStringValidator implements ConstraintValidator<EnumStringValues, String> {
    private Set<String> names = null;

    public EnumStringValidator() {
    }

    public void initialize(EnumStringValues constraintAnnotation) {
        this.names = new HashSet();
        String[] arrayStrings = constraintAnnotation.values();
        if (arrayStrings != null) {
            String[] var3 = arrayStrings;
            int var4 = arrayStrings.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String stringInArray = var3[var5];
                this.names.add(stringInArray);
            }
        }

        Class<? extends OursBaseStringEnum> enumedNameClass = constraintAnnotation.enumClass();
        if (enumedNameClass != null && enumedNameClass.isEnum() && enumedNameClass != OursBaseStringEnum.class) {
            OursBaseStringEnum[] enums = (OursBaseStringEnum[])enumedNameClass.getEnumConstants();
            OursBaseStringEnum[] var11 = enums;
            int var12 = enums.length;

            for(int var7 = 0; var7 < var12; ++var7) {
                OursBaseStringEnum enumItem = var11[var7];
                this.names.add(enumItem.getValue());
            }
        }

    }

    public boolean isValid(String name, ConstraintValidatorContext constraintValidatorContext) {
        return (name == null || name.equals("")) ? true : this.names.contains(name);
    }
}