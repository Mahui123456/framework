package com.krb.guaranty.common.context;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;

/**
 * 项目启动完成之后要异步执行一些初始化的操作(一个异步线程顺序按照order执行所有CommandLineRunner,ApplicationRunner的初始化)
 * @author owell
 * @date 2019/8/26 10:47
 */
public interface IFCAsyncInit extends CommandLineRunner,Ordered {

    @Override
    default void run(String... args) throws Exception{
        try {
            doInit(AppContext.getAppContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
}
