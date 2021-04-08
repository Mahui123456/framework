package com.krb.guaranty.common.business.excel;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 数据和sheet信息
 * @author xuqiang
 */
@Data
@AllArgsConstructor
public class SheetAndData {
    private String sheetName;
    private List dataList;
    private Class dataType;
}
