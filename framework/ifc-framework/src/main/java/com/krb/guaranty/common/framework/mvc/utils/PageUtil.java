package com.krb.guaranty.common.framework.mvc.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.krb.guaranty.common.framework.exception.ValidException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author owell
 * @date 2019/4/8 16:07
 */
public class PageUtil {
    public static <T> Page<T> page(HttpServletRequest request) {
        Integer page = 1;
        Integer size = 10;

        String pp = request.getHeader("page");
        String ss = request.getHeader("size");

        try {
            if (pp != null) {
                page = Integer.valueOf(pp);
            }
            if (ss != null) {
                size = Integer.valueOf(ss);
            }
        } catch (Exception e) {
            throw new ValidException("分页参数不合法!");
        }

        Page<T> pageBean = new Page<>(page, size);
        return pageBean;
    }
}
