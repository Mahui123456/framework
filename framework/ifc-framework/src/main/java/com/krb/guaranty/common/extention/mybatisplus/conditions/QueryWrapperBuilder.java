package com.krb.guaranty.common.extention.mybatisplus.conditions;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.OrderBySegmentList;
import com.krb.guaranty.common.utils.IFCJSONUtil;
import javafx.util.Callback;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * QueryWrapper 构造类
 * 1.QueryWrapperBuilder.empty() 会返回包装好的IFCQueryWrapper
 * 2.快捷构造查询条件方式:
 *     例如:
 *          public static QueryWrapper buildChannelPageWrapper(HttpServletRequest request, ChannelPageRequest body) {
 *              return QueryWrapperBuilder.of(
 *                 (condition) -> {
 *                     condition.put("channelCode", "biz_channel.channel_code like {0}",(value)->("%"+value+"%"));
 *                     condition.put("channelShortname", "biz_channel.channel_shortname like {0}",(value)->("%"+value+"%"));
 *                     condition.put("nstatus", "biz_channel.nstatus={0}");
 *                 },
 *                 (order) -> {
 *                     order.put("cooperationStartDate", "biz_channel_contract.cooperation_start_date");
 *                     order.put("cooperationEndDate", "biz_channel_contract.cooperation_end_date");
 *                     order.put("nstatus", "biz_channel.nstatus");
 *                     order.put("updateTime", "biz_channel.update_time");
 *                 },"biz_channel.update_time")
 *                 .conditionOfObject(body).orderOfHeader(request).build();
 *          }
 */
public class QueryWrapperBuilder {
    private String ascs;
    private String descs;
    private Object condition;
    private QueryWrapperConfigMap conditionMapping;
    private QueryWrapperConfigMap orderMapping;
    private Callback<QueryWrapper,QueryWrapper> defaultOrderCallback;

    private QueryWrapperBuilder() {
    }

    /**
     *
     * @param conditionConfig 映射的条件表达式map
     * @param orderConfig 映射的order字段map
     * @return
     */
    public static QueryWrapperBuilder of(QueryWrapperConditionConfig conditionConfig,QueryWrapperOrderConfig orderConfig) {
        return of(conditionConfig,orderConfig,(Callback)null);
    }

    public static <T> QueryWrapper<T> empty(){
        return new IFCQueryWrapper<>();
    }

    /**
     *
     * @param conditionConfig 映射的条件表达式map
     * @param orderConfig 映射的order字段map
     * @param defaultDescOrderFields 默认的倒序字段
     * @return
     */
    public static QueryWrapperBuilder of(QueryWrapperConditionConfig conditionConfig,QueryWrapperOrderConfig orderConfig,String ... defaultDescOrderFields) {
        QueryWrapperBuilder builder = new QueryWrapperBuilder();
        builder.conditionMapping = new QueryWrapperConfigMap();
        if(conditionConfig != null){
            conditionConfig.conditionMapping(builder.conditionMapping);
        }
        builder.orderMapping = new QueryWrapperConfigMap();
        if(orderConfig != null){
            orderConfig.orderMapping(builder.orderMapping);
        }

        if(defaultDescOrderFields != null && defaultDescOrderFields.length > 0){
            builder.defaultOrderCallback = (wrapper)->{
                builder.concatOrderToWrapper(false,defaultDescOrderFields,wrapper,true);
                return wrapper;
            };
        }
        return builder;
    }

    /**
     *
     * @param conditionConfig 映射的条件表达式map
     * @param orderConfig 映射的order字段map
     * @param defaultOrderCallback 如果没有排序字段后的回调
     * @return
     */
    public static QueryWrapperBuilder of(QueryWrapperConditionConfig conditionConfig,QueryWrapperOrderConfig orderConfig,Callback<QueryWrapper,QueryWrapper> defaultOrderCallback) {
        QueryWrapperBuilder builder = new QueryWrapperBuilder();
        builder.conditionMapping = new QueryWrapperConfigMap();
        if(conditionConfig != null){
            conditionConfig.conditionMapping(builder.conditionMapping);
        }
        builder.orderMapping = new QueryWrapperConfigMap();
        if(orderConfig != null){
            orderConfig.orderMapping(builder.orderMapping);
        }
        builder.defaultOrderCallback = defaultOrderCallback;
        return builder;
    }

    public QueryWrapperBuilder orderOfHeader(HttpServletRequest request) {
        this.ascs = request.getHeader("ascs");
        this.descs = request.getHeader("descs");
        return this;
    }

    public QueryWrapperBuilder orderOfQuery(HttpServletRequest request) {
        this.ascs = request.getParameter("ascs");
        this.descs = request.getParameter("descs");
        return this;
    }

    public QueryWrapperBuilder orderOfString(String ascs, String descs) {
        if (ascs != null) {
            this.ascs = ascs;
        }
        if (descs != null) {
            this.descs = descs;
        }
        return this;
    }

    public QueryWrapperBuilder conditionOfHeader(HttpServletRequest request) {
        String c = request.getHeader("condition");
        if (c != null && !c.equals("")) {
            this.condition = IFCJSONUtil.parseObject(c);
        }
        return this;
    }

    public QueryWrapperBuilder conditionOfQuery(HttpServletRequest request) {
        String c = request.getParameter("condition");
        if (c != null && !c.equals("")) {
            this.condition = IFCJSONUtil.parseObject(c);
        }
        return this;
    }

    /**
     * 对象必须得有setget方法,否则转换map不出来
     * @param condition
     * @return
     */
    public QueryWrapperBuilder conditionOfObject(Object condition) {
        this.condition = condition;
        return this;
    }

    public QueryWrapper build() {
        QueryWrapper wrapper = QueryWrapperBuilder.empty();
        //构造查询条件
        if (this.conditionMapping != null && condition != null) {
            Map<?, Object> map = null;
            if (condition instanceof Map) {
                map = (Map<Object, Object>) condition;
            } else {
                map = IFCJSONUtil.objectToMap(condition);
            }

            Object value = null;
            for (Map.Entry<String, QueryWrapperConfigMapValue> entry : this.conditionMapping.entrySet()) {
                value = map.get(entry.getKey());
                if(value == null){
                    continue;
                }
                value = entry.getValue().getValue(value);
                if (value.toString().equals("")) {
                    continue;
                }
                wrapper.apply(entry.getValue().getCondition(), value);
            }
        }

        //构造排序条件
        if (this.orderMapping != null) {
            if (ascs != null) {
                concatOrderToWrapper(true,ascs.split(","),wrapper,false);
            }

            if (descs != null) {
                concatOrderToWrapper(false,descs.split(","),wrapper,false);
            }
        }

        //如果没有排序条件回调默认的排序处理逻辑
        OrderBySegmentList orderList = wrapper.getExpression().getOrderBy();
        if(defaultOrderCallback != null){
            if(orderList == null || orderList.size()==0){
                defaultOrderCallback.call(wrapper);
            }
        }

        return wrapper;
    }

    /**
     * 连接排序到wrapper
     * @param isAsc 是否是正序
     * @param fields 排序字段
     * @param wrapper
     * @param noMappingField 未映射到的字段是否拼接原来的字段到order 语句中
     */
    private void concatOrderToWrapper(boolean isAsc, String [] fields, QueryWrapper wrapper,boolean noMappingField){
        if (fields != null && fields.length > 0) {
            Object value;
            QueryWrapperConfigMapValue mapValue;
            for (String orderField : fields) {
                mapValue = this.orderMapping.get(orderField);
                if (mapValue == null) {
                    if(noMappingField){
                        wrapper.orderBy(true,isAsc,orderField);
                    }
                    continue;
                }
                value = mapValue.getValue(mapValue.getCondition());
                if(value == null || value.equals("")){
                    continue;
                }

                wrapper.orderBy(true,isAsc,value);
            }
        }
    }


    /**
     * QueryWrapper条件配置类
     * 例如:
     * conditionMapping(){
     *      return new HashMap(){{
     *          //  key:javabean的字段名  value: 表名.字段名 表达式 {0}
     *          put("nstatus","nstatus={0}");
     *          put("channelContract.cooperationStartDate","biz_channel_contract.cooperation_start_date={0}");
     *      }}
     * }
     */
    public interface QueryWrapperConditionConfig {
        void conditionMapping(QueryWrapperConfigMap map);
    }

    /**
     * QueryWrapper条件配置类
     * 例如:
     * orderMapping(){
     *      return new HashMap(){{
     *          //  key:javabean的字段名  value: 表名.字段名
     *          put("nstatus","nstatus");
     *          put("channelContract.cooperationStartDate","biz_channel_contract.cooperation_start_date");
     *      }}
     * }
     */
    public interface QueryWrapperOrderConfig {
        void orderMapping(QueryWrapperConfigMap map);
    }

    public static class QueryWrapperConfigMapValue {
        String condition;
        Callback<Object,Object> valueCallback;

        public QueryWrapperConfigMapValue(String condition, Callback<Object, Object> valueCallback) {
            this.condition = condition;
            this.valueCallback = valueCallback;
        }

        public String getCondition() {
            return condition;
        }

        /**
         * value 是 null或者""空字符串不执行 callback函数
         * @param value
         * @return
         */
        public Object getValue(Object value){
            if(valueCallback != null && value!=null && !value.toString().equals("")){
                return valueCallback.call(value);
            }
            return value;
        }
    }

    public static class QueryWrapperConfigMap {
        private Map<String, QueryWrapperConfigMapValue> map = new LinkedHashMap<>();

        public QueryWrapperConfigMapValue put(String key, String value) {
            return this.put(key, value, null);
        }

        /**
         *
         * @param key
         * @param value
         * @param callback 对真正处理的字段值进行转换处理
         * @return
         */
        public QueryWrapperConfigMapValue put(String key, String value, Callback callback) {
            return map.put(key, new QueryWrapperConfigMapValue(value, callback));
        }

        public Set<Map.Entry<String, QueryWrapperConfigMapValue>> entrySet(){
            return map.entrySet();
        }

        public QueryWrapperConfigMapValue get(String key){
            return map.get(key);
        }
    }

}