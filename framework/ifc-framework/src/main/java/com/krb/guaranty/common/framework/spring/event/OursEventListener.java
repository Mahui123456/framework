package com.krb.guaranty.common.framework.spring.event;

import com.krb.guaranty.common.context.AppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.Map;


/**
 * spring 事件监听器
 * @param <E>
 */
public abstract class OursEventListener<E extends OursEvent> implements ApplicationListener<E> {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public final void onApplicationEvent(E event) {
		Map<String, Object> threadVar = event.getThreadVar();
		if (threadVar != null && threadVar.size() > 0) {
			AppContext.putAllThreadContext(threadVar);
		}

		try {
			this.doEvent(event);
		} catch (Exception e) {
			logger.error("事件执行发生异常...",e);
			if(e instanceof RuntimeException){
				throw (RuntimeException)e;
			}else{
				throw new RuntimeException(this.getClass().getName(),e);
			}
		}finally {
			//异步需要清理线程变量
			if (!event.isSync()) {
				AppContext.clearThreadContext();
			}
		}

	}

	public abstract void doEvent(E event) throws Exception;
}