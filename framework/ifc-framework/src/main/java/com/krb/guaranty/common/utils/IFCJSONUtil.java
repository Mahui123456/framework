package com.krb.guaranty.common.utils;

import cn.hutool.core.bean.BeanUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * json 工具包
 * gson实现
 * @author owell
 * @date 2020/6/4 14:10
 */
public class IFCJSONUtil {

    /**
     * 默认的gosn 转换对象
     */
    private static final Gson GSON;

    static {

        GsonBuilder builder = new GsonBuilder().disableHtmlEscaping();

        //日期类型 时间戳 互转
        /*builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });

        builder.registerTypeAdapter(Date.class,new JsonSerializer<Date> () {
            public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.getTime());
            }
        });*/

        GSON = builder.setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }


    /**
     * 转换成json字符串
     * @param object
     * @return
     */
    public static String toJSONString(Object object) {
        return GSON.toJson(object);
    }

    /**
     * 转换成json字符串(保留NULL字段)
     * @param object
     * @return
     */
    public static String toJSONStringWithNull(Object object){
        return new GsonBuilder().disableHtmlEscaping().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(object);
    };

    /**
     * json 字符串转换对象
     * @param json
     * @return
     * @throws JsonSyntaxException
     */
    public static Object parseObject(String json) throws JsonSyntaxException {
        return GSON.fromJson(json,Object.class);
    }

    /**
     * json 字符串转换对象
     * @param json
     * @param classOfT
     * @param <T>
     * @return
     * @throws JsonSyntaxException
     */
    public static <T> T parseObject(String json, Class<T> classOfT) throws JsonSyntaxException {
        return GSON.fromJson(json,classOfT);
    }

    /**
     * json 转 map
     * @param json
     * @return
     */
    public static Map<String,Object> toMap(String json){
        return GSON.fromJson(json,TypeToken.getParameterized(Map.class, String.class,Object.class).getType());
    }


    /**
     * json 转 map
     * @param json
     * @return
     */
    public static <K,V> Map<K,V> toMap(String json, Class<K> classOfK, Class<V> classOfV){
        return GSON.fromJson(json,TypeToken.getParameterized(Map.class, classOfK , classOfV).getType());
    }

    /**
     * 对象转map
     * @param obj
     * @return
     */
    public static Map<String, Object> objectToMap(Object obj){
        if(obj == null){
            return Collections.emptyMap();
        }

        return BeanUtil.beanToMap(obj);
    }

    /**
     * map 转对象
     * @param map
     * @param classOfT
     * @param <T>
     * @return
     */
    public static <T> T mapToObject(Map map,Class<T> classOfT){
        T t = null;
        try {
            t = classOfT.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            BeanUtils.populate(t,map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return t;
    }


    /**
     * 转换成list对象
     * @param json
     * @param classOfT
     * @param <T>
     * @return
     */
    public static <T> List<T> parseArray(String json, Class<T> classOfT) {
        return GSON.fromJson(json,TypeToken.getParameterized(List.class, classOfT).getType());
    }

    public static Gson getGSON() {
        return GSON;
    }
}
