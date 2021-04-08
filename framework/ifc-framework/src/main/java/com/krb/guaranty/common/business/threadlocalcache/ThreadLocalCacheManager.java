package com.krb.guaranty.common.business.threadlocalcache;

import org.springframework.cache.support.AbstractCacheManager;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * 线程级别的缓存
 * @author owell
 * @date 2020/7/23 10:07
 */
public class ThreadLocalCacheManager extends AbstractCacheManager {

    @Override
    protected Collection<ThreadLocalCache> loadCaches() {
        List<ThreadLocalCache> caches = new LinkedList<>();
        caches.add(new ThreadLocalCache());
        return caches;
    }

}
