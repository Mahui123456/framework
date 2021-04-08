package com.krb.guaranty.common.framework.mvc.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 删除标识
 *
 * @author luzhijie
 */
@Getter
@AllArgsConstructor
public enum EnumDelete implements OursBaseStringEnum {
    /**
     * 未删除
     */
    N("N", "未删除"),
    /**
     * 已删除
     */
    Y("Y", "已删除");


    @EnumValue
    @JsonValue
    private String value;

    private String message;

}

