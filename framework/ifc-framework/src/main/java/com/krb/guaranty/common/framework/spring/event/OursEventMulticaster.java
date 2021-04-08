package com.krb.guaranty.common.framework.spring.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component("applicationEventMulticaster")
public class OursEventMulticaster extends SimpleApplicationEventMulticaster {
	private static final Logger logger = LoggerFactory.getLogger(OursEventMulticaster.class);
	private ErrorHandler errorHandler = new ErrorHandler(){
		@Override
		public void handleError(Throwable throwable) {
			OursEventMulticaster.logger.error("事件执行发生异常", throwable);
		}
	};
	//返回null则父类会把异常抛出去
	protected ErrorHandler getErrorHandler() {
		//return this.errorHandler;
		return null;
	}

	//异步执行线程池
	private ExecutorService executorService = Executors.newFixedThreadPool(50);

	public void multicastEvent(ApplicationEvent event, ResolvableType eventType) {
		if (event instanceof OursEvent) {
			OursEvent ev = (OursEvent) event;
			this.exec(ev, !ev.isSync());
		} else {
			this.exec(event, true);
		}

	}

	private void exec(final ApplicationEvent event, boolean async) {
		Iterator<ApplicationListener<?>> arg2 = this.getApplicationListeners(event, ResolvableType.forInstance(event)).iterator();

		while (arg2.hasNext()) {
			final ApplicationListener<?> listener = arg2.next();
			if (async) {
				this.executorService.execute(new Runnable() {
					@Override
					public void run() {
						OursEventMulticaster.this.invokeListener(listener, event);
					}
				});
			} else {
				this.invokeListener(listener, event);
			}
		}

	}
}
