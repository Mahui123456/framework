package com.krb.guaranty.common.business.lock.service;

import com.krb.guaranty.common.constant.RedisConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LockService {
    private static final Logger logger = LoggerFactory.getLogger(LockService.class);

    public final static String LOCK_PREFIX = RedisConstant.LOCK_PREFIX;

    //毫秒,默认锁定时间是一分钟
    private final static Long DEFAULT_LOCK_EXPIRE = RedisConstant.DEFAULT_CACHE_EXPIRE_ONE_MINUTE;

    @Autowired(required = false)
    private RedisTemplate redisTemplate;

    /**
     * 获取锁,默认失效时间是60秒
     * @param key 锁key
     * @return true:获取成功,false:获取失败
     */
    public boolean getLock(String key) {
        return getLock(key, null);
    }

    /**
     * 获取锁
     * @param key 锁key
     * @param expire 超时时间,null 则默认是60秒
     * @return true:获取成功,false:获取失败
     */
    public boolean getLock(String key, Long expire) {
        if(expire == null){
            expire = DEFAULT_LOCK_EXPIRE;
        }

        String lock = LOCK_PREFIX + key;
        Long finalExpire = expire;
        boolean locked = (Boolean) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                byte[] key = lock.getBytes();
                long expireAt = System.currentTimeMillis() + finalExpire + 1;
                Boolean acquire = redisConnection.setNX(key, String.valueOf(expireAt).getBytes());
                if (acquire) {
                    redisConnection.pExpire(key,finalExpire+1);
                    return true;
                } else {
                    //获取缓存中上一次放进去的失效时间,
                    byte[] value = redisConnection.get(key);
                    if (Objects.nonNull(value) && value.length > 0) {
                        //转换成long类型
                        long expireTime = Long.parseLong(new String(value));
                        //如果缓存中放进去的时间戳比现在小,则更新缓存中的时间戳
                        if (expireTime < System.currentTimeMillis()) {
                            byte[] oldValue = redisConnection.getSet(key, String.valueOf(System.currentTimeMillis() + finalExpire + 1).getBytes());
                            redisConnection.pExpire(key,finalExpire+1);
                            return Long.parseLong(new String(oldValue)) < System.currentTimeMillis();
                        }
                    }
                }
                return false;
            }
        });
        if (locked) {
            logger.info("获取锁成功 lock_key={}", lock);
        } else {
            logger.info("获取锁失败 lock_key={}", lock);
        }
        return locked;
    }

    public void releaseLock(String key) {
        String lock = LOCK_PREFIX + key;
        try {
            redisTemplate.delete(lock);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取一个简单的锁,60s 后自动过期
     * @param key
     */
    public boolean getSimpleLock(String key){
        return getSimpleLock(key,null);
    }

    /**
     * 获取一个简单的锁,过期自动超时
     * @param key
     * @param expire null 默认是60s
     */
    public boolean getSimpleLock(String key,Long expire){
        if(expire == null){
            expire = DEFAULT_LOCK_EXPIRE;
        }

        boolean locked = redisTemplate.opsForValue().setIfAbsent(LOCK_PREFIX + key,1,expire, TimeUnit.MILLISECONDS);
        if (locked) {
            logger.info("获取锁成功 lock_key={}", LOCK_PREFIX + key);
        } else {
            logger.info("获取锁失败 lock_key={}", LOCK_PREFIX + key);
        }
        return locked;
    }

    public void releaseSimpleLock(String key) {
        releaseLock(key);
    }
}