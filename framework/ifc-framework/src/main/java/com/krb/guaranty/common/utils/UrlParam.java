package com.krb.guaranty.common.utils;

import javafx.util.Callback;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * url参数组装
 * @author owell
 * @date 2019/8/12 16:14
 */
public class UrlParam {
    private List<Pair<String,Object>> params = new ArrayList<>();

    public UrlParam() {
    }

    public UrlParam(Map<String,Object> param) {
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            this.addParamNotEmpty(entry.getKey(),entry.getValue());
        }
    }

    public UrlParam addParam(String key , Object value){
        if(value != null){
            params.add(new Pair<>(key,value));
        }
        return this;
    }

    public UrlParam addParamNotEmpty(String key ,Object value){
        if(value != null && StringUtils.isNotEmpty(value.toString())){
            params.add(new Pair<>(key,value));
        }
        return this;
    }

    public UrlParam addParamNotBlank(String key ,Object value){
        if(value != null && StringUtils.isNotBlank(value.toString())){
            params.add(new Pair<>(key,value));
        }
        return this;
    }

    public <V> UrlParam addParam(String key , V value, Callback<V,Boolean> callback){
        Boolean canAdd = callback.call(value);
        if(canAdd != null && canAdd){
            params.add(new Pair<>(key,value));
        }
        return this;
    }

    public String toUrlParamString(){
        if(params != null && params.size()>0){
            StringBuffer ss = new StringBuffer();
            for (Pair<String, Object> param : params) {
                ss.append("&").append(param.getKey()).append("=").append(param.getValue());
            }

            return ss.replace(0,1,"?").toString();
        }
        return "";
    }

    @Override
    public String toString() {
        return this.toUrlParamString();
    }

    public static String toUrlParamString(Map<String,Object> param){
        return new UrlParam(param).toUrlParamString();
    }
}