package com.krb.guaranty.common.business.asyncbatchpersistence.service;

import com.krb.guaranty.common.business.asyncbatchpersistence.configuration.AsyncBatchPersistenceAutoConfig;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * 异步批量刷数据的基础类
 * 处理流程是先写入缓存,然后定时任务隔一段时间批量持久化
 * @author owell
 * @date 2019/8/19 15:16
 */
public interface AsyncBatchPersistenceService<E> {

    Logger log = LoggerFactory.getLogger(AsyncBatchPersistenceService.class);

    /**
     * 批量写
     * @param entity
     */
    Object batchPersistence(Collection<E> entity);

    /**
     * 写入缓存的key
     * @return
     */
    String getBaseAsyncBatchPersistenceKey();

    /**
     * 定时任务执行时间,默认是一分钟一次
     * @return
     */
    default String getBatchPersistenceCron(){
        return "45 */1 * * * ?";
    }

    /**
     * 写入缓存的key
     * @return
     */
    default String getAsyncBatchPersistenceKey(){
        return "BATCH_PERSISTENCE:".concat(getBaseAsyncBatchPersistenceKey());
    }

    /**
     * 获取redisTemplate
     * @return
     */
    RedisTemplate getRedisTemplate();

    /**
     * 执行批量写,一直消费完(最大消费次数)
     * @return key:从缓存中拿出来的需要保存的记录  value:执行批量保存方法的返回值
     */
    default int runBatchPersistence(){
        String key = this.getAsyncBatchPersistenceKey();
        final Logger log = getLog();
        log.info("批量持久化准备执行,key:{} start ...",key);
        int total = 0;
        //是否全都持久化完成
        boolean hasFinish = false;
        for(int i = 1;i<= this.getMaxRunBatchPersistenceCount() && !hasFinish;i++){
            Pair<Collection<E>, Object> result = runBatchPersistenceOne(key,i,getBatchPersistenceSize(),log);
            hasFinish = CollectionUtils.isEmpty(result.getKey());
            if(!hasFinish){
                total += result.getKey().size();
            }else{
                log.info("批量持久化第{}批执行,本批全部完成共:{}条",i,total);
            }
        }
        log.info("批量持久化执行完成 finish ...");
        return total;
    }

    default Pair<Collection<E>, Object> runBatchPersistenceOne(){
        return runBatchPersistenceOne(getAsyncBatchPersistenceKey(),1,getBatchPersistenceSize(),getLog());
    }

    /**
     * 执行一次持久化
     * @param key 缓存key
     * @param i 第几批
     * @param size 一批写入多少条
     * @return key:从缓存中拿出来的需要保存的记录  value:执行批量保存方法的返回值
     */
    default Pair<Collection<E>, Object> runBatchPersistenceOne(String key,Integer i,Integer size,Logger log){
        //1.拿到数据
        Collection entity = this.getBatchPersistenceEntity(size);
        Object result = null;
        try{
            if(!CollectionUtils.isEmpty(entity)){
                //2.持久化数据
                result = this.batchPersistence(entity);
                log.info("批量持久化第{}批执行,key:{},保存:{}条",i,key,entity.size());
            }
        }catch (Exception e){
            //出异常了则把这批数据写到异常缓存里避免数据丢失
            getRedisTemplate().opsForList().rightPushAll("ERROR:".concat(key),entity);
            log.error("批量持久化异常第{}批执行,异常写入到:{}",i,"ERROR:".concat(key));
            throw e;
        }
        return new Pair<Collection<E>,Object>(entity,result);
    }

    /**
     * 临时写到缓存中后面批量持久化
     * @param entity
     */
    default void addEntityToCache4AsyncBatchPersistence(E entity){
        if(this.batchPersistence()){
            getRedisTemplate().opsForList().rightPush(getAsyncBatchPersistenceKey(),entity);
        }else{
            this.save(entity);
        }
    }

    public boolean save(E entity);

    public boolean saveBatch(Collection<E> entityList, int batchSize);

    /**
     * 临时写到缓存中后面批量持久化
     * @param entity
     */
    default void addEntityToCache4AsyncBatchPersistence(Collection<E> entity){
        if(this.batchPersistence()){
            getRedisTemplate().opsForList().rightPushAll(getAsyncBatchPersistenceKey(),entity);
        }else{
            this.saveBatch(entity,getBatchPersistenceSize());
        }
    }

    /**
     * 获取需要异步保存的数据
     * @return
     */
    default Collection<E> getBatchPersistenceEntity(int size) {
        final String key = getAsyncBatchPersistenceKey();
        return (Collection<E>) getRedisTemplate().execute(new SessionCallback<Collection<E>>() {
            @Override
            public Collection<E> execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.opsForList().range(key, 0, size);
                operations.opsForList().trim(key,size,-1);
                return (Collection<E>) operations.exec().get(0);
            }
        });
    }

    /**
     * 每次批量操作的数据条数
     * @return
     */
    default int getBatchPersistenceSize(){
        return 100;
    }

    /**
     * 最多执行批量持久化方法的次数
     * @return
     */
    default int getMaxRunBatchPersistenceCount(){
        return 1000;
    }

    default Logger getLog(){
        return log;
    }

    /**
     * 是否开启批量持久化
     * @return 默认开启
     */
    default boolean batchPersistence(){
        return AsyncBatchPersistenceAutoConfig.isBatchPersistence();
    }
}
