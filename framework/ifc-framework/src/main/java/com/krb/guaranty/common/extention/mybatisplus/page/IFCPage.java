package com.krb.guaranty.common.extention.mybatisplus.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.krb.guaranty.common.framework.exception.ValidException;
import com.krb.guaranty.common.utils.IFCJSONUtil;
import com.krb.guaranty.common.utils.RequestUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author owell
 * @date 2019/1/10 11:15
 */
@Data
public class IFCPage<T> extends Page<T> {
    private Map<Object,Object> condition;

    public IFCPage<T> setOrderByField(String orderByField) {
        if (StringUtils.isNotEmpty(orderByField)) {
            this.setDesc(orderByField);
        }
        return this;
    }

    public Map<String,Object> toMap(){
        return IFCPage.pageToMap(this);
    }

    public static Map<String,Object> pageToMap(Page page){
        Map<String,Object> map  = new HashMap<String, Object>();
        map.put("records",page.getRecords());
        map.put("total",page.getTotal());
        map.put("size",page.getSize());
        map.put("pages",page.getPages());
        map.put("current",page.getCurrent());

        return map;
    }

    public IFCPage(){
        super();
    }

    public IFCPage(int current, int size) {
        super(current, size);
    }

    public IFCPage(int current, int size, String orderByField) {
        this(current, size);
        this.setOrderByField(orderByField);
    }

    /**
     * 从request里直接初始化分页参数
     * @param request
     */
    public IFCPage(HttpServletRequest request){
        this(getCurrent(request),getSize(request));

        //从request里直接初始化条件
        buildCondition(request);

        //初始化排序字段
        buildOrderByField(request);
    }

    public IFCPage buildCondition(HttpServletRequest request){
        String conditionJSON = RequestUtil.getString(request,"condition");
        if(StringUtils.isEmpty(conditionJSON)) {
            return this;
        }
        Map<String,Object> condition = IFCJSONUtil.toMap(conditionJSON);
        if(condition == null || condition.size()==0) {
            return this;
        }
        String value = null;
        this.setCondition(new LinkedHashMap<>());
        for (Map.Entry<String, Object> entry:condition.entrySet()) {
            if(entry.getValue() == null) {
                continue;
            }
            value = entry.getValue().toString();
            if(StringUtils.isNotBlank(value) && !value.equals("%") && !value.equals("%%")){
                if(entry.getKey().contains("{0}")){
                    this.getCondition().put(entry.getKey(),entry.getValue());
                }else{
                    this.getCondition().put(entry.getKey()+"={0}",entry.getValue());
                }
            }
        }
        return this;
    }

    public IFCPage buildOrderByField(HttpServletRequest request){
        String orderByField = RequestUtil.getString(request,"orderByField");
        this.setOrderByField(orderByField);
        return this;
    }

    private static int getCurrent(HttpServletRequest request){ // 页码
        int page = 1;
        String pp = request.getParameter("page");
        if(pp == null){
            pp = request.getHeader("page");
        }
        try {
            if (pp != null) {
                page = Integer.valueOf(pp);
            }
        } catch (Exception e) {
            throw new ValidException("分页参数不合法!");
        }
        return page;
    }

    private static int getSize(HttpServletRequest request){
        int size = 10;
        String ss = request.getParameter("size");
        if(ss == null){
            ss = request.getHeader("size");
        }
        try {
            if (ss != null) {
                size = Integer.valueOf(ss);
            }
        } catch (Exception e) {
            throw new ValidException("分页参数不合法!");
        }
        return size;
    }

    @Override
    public Map<Object, Object> condition() {
        return this.getCondition();
    }

}
