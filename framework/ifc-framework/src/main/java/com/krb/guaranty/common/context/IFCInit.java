package com.krb.guaranty.common.context;

import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;

/**
 * 项目启动将要启动完成同步执行一些初始化的操作
 * 注意:执行完成之后项目才会启动完成,异常会输出error日志不会影响启动
 * @author owell
 * @date 2019/8/26 10:47
 */
public interface IFCInit extends Comparable<IFCInit>,Ordered {

    void doInit(ApplicationContext application) throws Exception;

    /**
     * bean初始化的顺序,数值越小越先初始化
     *
     * @return
     */
    @Override
    default int getOrder() {
        return 0;
    }

    @Override
    default int compareTo(IFCInit o){
        return Integer.valueOf(this.getOrder()).compareTo(o.getOrder());
    }
}
