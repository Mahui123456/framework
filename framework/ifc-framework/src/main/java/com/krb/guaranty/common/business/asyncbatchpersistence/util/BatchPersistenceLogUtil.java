package com.krb.guaranty.common.business.asyncbatchpersistence.util;

import com.krb.guaranty.common.business.asyncbatchpersistence.service.AsyncBatchPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author owell
 * @date 2019/8/28 10:30
 */
public class BatchPersistenceLogUtil {
    private static final Map<AsyncBatchPersistenceService,Logger> logMap = new ConcurrentHashMap<>();

    public static Logger getLog(AsyncBatchPersistenceService service){
        Logger logger = logMap.get(service);
        if(logger == null){
            logger = LoggerFactory.getLogger(service.getClass());
            logMap.put(service,logger);
        }
        return logger;
    }
}
