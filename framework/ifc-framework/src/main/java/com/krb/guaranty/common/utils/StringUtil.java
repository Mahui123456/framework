package com.krb.guaranty.common.utils;


import org.apache.commons.lang3.StringUtils;

/**
 * StringUtils 扩展类
 */
public class StringUtil extends StringUtils {

    public static boolean isNotBlank(String obj) {
        if(StringUtils.isNotBlank(obj) && !"null".equals(obj)){
            return true;
        }
        return false;
    }

    /**
     * 如果是null 返回null 否则返回toString
     * @param obj
     * @return 如果是null 返回null 否则返回toString
     */
    public static String valueOfNull(Object obj){
        return (obj == null) ? null : obj.toString();
    }

    /**
     * 解析数组组装下载ids
     * @param strings
     * @return
     */
    public static String buildIds(String[] strings){
        String str = null;

        if(strings.length == 1){
            str = "("+strings[0] + ")";
            return str;
        }
        for (int i = 0; i < strings.length; i++) {
            if(i == 0){
                str = "("+strings[i];
            }else if(i != 0 && i != strings.length-1){
                str = str + "," + strings[i];
            }else {
                str = str + ")";
            }
        }

        return str;
    }

}
