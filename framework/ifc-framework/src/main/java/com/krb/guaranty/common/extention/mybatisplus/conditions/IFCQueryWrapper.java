package com.krb.guaranty.common.extention.mybatisplus.conditions;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.conditions.segments.NormalSegmentList;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.util.Objects;

/**
 * @author owell
 * @date 2019/4/10 15:34
 */
public class IFCQueryWrapper<T> extends QueryWrapper<T> {

    /**
     * 和 getCustomSqlSegment 功能类似,getCustomSqlSegment是拼接where 当前方法是拼接and
     *
     * 获取自定义SQL 简化自定义XML复杂情况
     * 使用方法
     * `自定义sql` + ${ew.customSqlSegment}
     * <p>1.逻辑删除需要自己拼接条件 (之前自定义也同样)</p>
     * <p>2.不支持wrapper中附带实体的情况 (wrapper自带实体会更麻烦)</p>
     * <p>3.用法 ${ew.customSqlSegment} (不需要and标签包裹,切记!)</>
     * <p>4.ew是wrapper定义别名,可自行替换</>
     */
    public String getCustomAndSqlSegment(){
        MergeSegments expression = getExpression();
        if (Objects.nonNull(expression)) {
            NormalSegmentList normal = expression.getNormal();
            String sqlSegment = getSqlSegment();
            if (StringUtils.isNotEmpty(sqlSegment)) {
                if (normal.isEmpty()) {
                    return sqlSegment;
                } else {
                    return "AND "+ sqlSegment;
                }
            }
        }
        return StringUtils.EMPTY;
    }
}
