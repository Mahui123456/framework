package com.krb.guaranty.common.business.snowflake.service;

import com.krb.guaranty.common.business.snowflake.exception.SnowflakeException;
import com.krb.guaranty.common.business.snowflake.model.IdMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 有状态的service， 必须是单例
 */
@Service
public class SnowflakeIdService{
	private static final Logger log = LoggerFactory.getLogger(SnowflakeIdService.class);
	
	@Autowired
    private WorkIdProvider workIdProvider;

    private long sequence = 0L;

    private long lastTimestamp = -1L;

    private Lock lock = new ReentrantLock();

    public long nextId() {
        final Lock lock = this.lock;
        lock.lock();
        try {
            return doMakeNextId();
        } finally {
            lock.unlock();
        }
    }

    private long doMakeNextId() {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            log.error("[snowflake] 时间回退， timestamp={}, lastTimestamp={}",
                    timestamp, lastTimestamp);
            throw new SnowflakeException("当前时间回退, 请检查");
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & IdMetadata.SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;
        return makeId(sequence, lastTimestamp);
    }

    public long makeId(long seq, long timestamp) {
        long ret = 0;
        ret |= seq;
        ret |= workIdProvider.getWorkId() << IdMetadata.WORKER_BIT;
        ret |= (timestamp - IdMetadata.TIME_START) << IdMetadata.TIMESTAMP_BIT;
        return ret;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    public void setWorkIdProvider(WorkIdProvider workIdProvider) {
        this.workIdProvider = workIdProvider;
    }
}
