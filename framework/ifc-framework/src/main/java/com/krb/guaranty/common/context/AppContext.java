package com.krb.guaranty.common.context;

import com.krb.guaranty.common.business.threadlocalcache.ThreadLocalCache;
import com.krb.guaranty.common.framework.spring.event.OursEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AppContext implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(AppContext.class);

    private static ApplicationContext ac;
    private static final Map<String, Object> beanCacheMap = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Object> beanClassCacheMap = new ConcurrentHashMap<>();
    private static final ThreadLocal<Map<String, Object>> threadVar = new ThreadLocal<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("初始化开始 start ....");
        AppContext.ac = applicationContext;

        Map<String, IFCInit> initMap = applicationContext.getBeansOfType(IFCInit.class);
        if(!CollectionUtils.isEmpty(initMap)){
            List<IFCInit> beans = new ArrayList<>(initMap.values());
            Collections.sort(beans);
            for (IFCInit initBean : beans) {
                try {
                    initBean.doInit(applicationContext);
                    log.info("bean:[{}],init完成",initBean,initBean.getOrder());
                } catch (Exception e) {
                    log.error("bean:[{}],init失败...",initBean,initBean.getOrder(),e);
                }
            }
        }
        log.info("执行IFCInit完成,共{}个",initMap == null ? 0 : initMap.size());

        log.info("初始化完成 finish ....");
    }

    public static ApplicationContext getAppContext() {
        if(ac == null){
            throw new RuntimeException("ApplicationContext未初始化");
        }
        return ac;
    }

    /**
     * 调用spring发送事件,最好只在事件里处理一些不影响核心业务的逻辑,OursEventMulticaster 固定50线程池(谨慎使用同步事件)
     * @param event
     * @param sync true:同步事件(所有监听同步事件的listener都在同一个事务里,并且同步listener的异常可以报出来,谨慎使用!!!),false:异步事件
     */
    public static void publishEvent(OursEvent event, boolean sync) {
        if (sync) {
            event.sync();
        } else {
            event.setThreadVar(threadVar.get());
        }
        //避免异步事件调用的时候事务还未提交导致的问题
        //如果不是同步,并且开启事物了(如果是同步则让他们都在一个事物里)
        if(!sync&&TransactionSynchronizationManager.isSynchronizationActive()){
            //在事物执行成功后发布事件
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    getAppContext().publishEvent(event);
                }
            });
        }else{
            getAppContext().publishEvent(event);
        }
    }

    public static void putAllThreadContext(Map<String, Object> varMap) {
        if (varMap != null && varMap.size() > 0) {
            threadVar.set(varMap);
        }
    }

    /**
     * 在线程执行完成一定要调用 AppContext.clearThreadContext()
     * 否则当前线程变量会被下一个请求复用
     * @param key
     * @param value
     */
    public static void putThreadContext(String key, Object value) {
        Map<String, Object> threadMap = threadVar.get();
        if (threadMap == null) {
            threadMap = new HashMap<>();
            threadVar.set(threadMap);
        }

        threadMap.put(key, value);
    }

    public static Object getThreadContext(String key) {
        Map<String, Object> threadMap = threadVar.get();
        return threadMap == null ? null : threadMap.get(key);
    }

    public static Map<String, Object> getAllThreadContext() {
        return threadVar.get();
    }

    public static void removeThreadContext(String key) {
        Map<String, Object> threadMap = threadVar.get();
        if (threadMap != null) {
            threadMap.remove(key);
        }

    }

    public static void clearThreadContext() {
        threadVar.remove();
        ThreadLocalCache.clearThreadContext();
        UserAuthorityUtil.clearThreadContext();
    }

    private static Object getBeanByName(String beanName) {
        if (beanCacheMap != null) {
            Object bean = beanCacheMap.get(beanName);// 273
            if (bean == null) {
                try {
                    bean = getAppContext().getBean(beanName);
                    beanCacheMap.put(beanName, bean);
                } catch (Exception e) {
                    e.printStackTrace();
                    bean = null;
                }
            }
            return bean;
        }
        return null;
    }

    public static <T> T getBeanByName(String beanName, Class<T> c) {
        return c.cast(getBeanByName(beanName));
    }

    public static <T> T getBeanByClass(Class<T> c) {
        if (beanClassCacheMap != null) {
            Object bean = beanClassCacheMap.get(c);// 273
            if (bean == null) {
                try {
                    bean = getAppContext().getBean(c);
                    beanClassCacheMap.put(c, bean);
                } catch (Exception arg2) {
                    arg2.printStackTrace();
                    bean = null;
                }
            }
            return c.cast(bean);
        }
        return null;
    }

    public static HttpServletRequest getRequest(){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(requestAttributes != null){
            return requestAttributes.getRequest();
        }
        return null;
    }

    public static HttpServletResponse getResponse(){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(requestAttributes != null){
            return requestAttributes.getResponse();
        }
        return null;
    }

    public static boolean findIsPro(){
        return OursVarConfig.getSpringActive().equals("prod");
    }

    public static boolean findIsTest(){
        return !findIsPro();
    }
}
