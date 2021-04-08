package com.krb.guaranty.common.business.threadlocalcache;

import com.krb.guaranty.common.context.AppContext;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.core.serializer.support.SerializationDelegate;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;

/**
 * 线程级别的缓存
 * @author owell
 * @date 2020/7/23 10:07
 * @see org.springframework.cache.concurrent.ConcurrentMapCache
 */
public class ThreadLocalCache extends AbstractValueAdaptingCache {

    private final String name;

    private static final ThreadLocal<Map<Object, Object>> threadVar = new ThreadLocal<>();

    @Nullable
    private final SerializationDelegate serialization;

    public ThreadLocalCache() {
        this("threadLocalCache");
    }

    /**
     * Create a new ConcurrentMapCache with the specified name.
     * @param name the name of the cache
     */
    public ThreadLocalCache(String name) {
        this(name, true);
    }

    /**
     * Create a new ConcurrentMapCache with the specified name and the
     * given internal {@link ConcurrentMap} to use.
     * @param name the name of the cache
     * @param allowNullValues whether to allow {@code null} values
     * (adapting them to an internal null holder value)
     */
    public ThreadLocalCache(String name, boolean allowNullValues) {
        this(name, allowNullValues, null);
    }

    /**
     * Create a new ConcurrentMapCache with the specified name and the
     * given internal {@link ConcurrentMap} to use. If the
     * {@link SerializationDelegate} is specified,
     * {@link #isStoreByValue() store-by-value} is enabled
     * @param name the name of the cache
     * @param allowNullValues whether to allow {@code null} values
     * (adapting them to an internal null holder value)
     * @param serialization the {@link SerializationDelegate} to use
     * to serialize cache entry or {@code null} to store the reference
     * @since 4.3
     */
    protected ThreadLocalCache(String name,
                               boolean allowNullValues, @Nullable SerializationDelegate serialization) {

        super(allowNullValues);
        Assert.notNull(name, "Name must not be null");
        this.name = name;
        this.serialization = serialization;
    }


    /**
     * Return whether this cache stores a copy of each entry ({@code true}) or
     * a reference ({@code false}, default). If store by value is enabled, each
     * entry in the cache must be serializable.
     * @since 4.3
     */
    public final boolean isStoreByValue() {
        return (this.serialization != null);
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final Map<Object, Object> getNativeCache() {
        Map<Object, Object> map = threadVar.get();
        if(map == null){
            map = new HashMap<>();
            threadVar.set(map);
        }
        return map;
    }

    @Override
    @Nullable
    protected Object lookup(Object key) {
        return this.getNativeCache().get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> T get(Object key, Callable<T> valueLoader) {
        return (T) fromStoreValue(this.getNativeCache().computeIfAbsent(key, k -> {
            try {
                return toStoreValue(valueLoader.call());
            }
            catch (Throwable ex) {
                throw new ValueRetrievalException(key, valueLoader, ex);
            }
        }));
    }

    @Override
    public void put(Object key, @Nullable Object value) {
        this.getNativeCache().put(key, toStoreValue(value));
    }

    @Override
    @Nullable
    public ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        Object existing = this.getNativeCache().putIfAbsent(key, toStoreValue(value));
        return toValueWrapper(existing);
    }

    @Override
    public void evict(Object key) {
        this.getNativeCache().remove(key);
    }

    @Override
    public void clear() {
        this.getNativeCache().clear();
    }

    @Override
    protected Object toStoreValue(@Nullable Object userValue) {
        Object storeValue = super.toStoreValue(userValue);
        if (this.serialization != null) {
            try {
                return serializeValue(this.serialization, storeValue);
            }
            catch (Throwable ex) {
                throw new IllegalArgumentException("Failed to serialize cache value '" + userValue +
                        "'. Does it implement Serializable?", ex);
            }
        }
        else {
            return storeValue;
        }
    }

    private Object serializeValue(SerializationDelegate serialization, Object storeValue) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            serialization.serialize(storeValue, out);
            return out.toByteArray();
        }
        finally {
            out.close();
        }
    }

    @Override
    protected Object fromStoreValue(@Nullable Object storeValue) {
        if (storeValue != null && this.serialization != null) {
            try {
                return super.fromStoreValue(deserializeValue(this.serialization, storeValue));
            }
            catch (Throwable ex) {
                throw new IllegalArgumentException("Failed to deserialize cache value '" + storeValue + "'", ex);
            }
        }
        else {
            return super.fromStoreValue(storeValue);
        }

    }

    private Object deserializeValue(SerializationDelegate serialization, Object storeValue) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream((byte[]) storeValue);
        try {
            return serialization.deserialize(in);
        }
        finally {
            in.close();
        }
    }

    /**
     * @see AppContext#clearThreadContext()
     */
    public static void clearThreadContext() {
        threadVar.remove();
    }
}
