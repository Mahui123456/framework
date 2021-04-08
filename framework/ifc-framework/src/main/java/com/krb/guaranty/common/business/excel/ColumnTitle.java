package com.krb.guaranty.common.business.excel;

import java.lang.annotation.*;

/**
 * @author xuqiang
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ColumnTitle {
    /**
     * 列名
     * @return
     */
    String name();

    /**
     * 列名
     * @return
     */
    int index();
}
