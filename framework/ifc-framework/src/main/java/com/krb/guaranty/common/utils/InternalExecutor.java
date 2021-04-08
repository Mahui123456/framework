package com.krb.guaranty.common.utils;

import com.krb.guaranty.common.framework.exception.GuarantyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 重试执行器
 * @author owell
 *
 * @param <T> 执行完之后的返回值
 */
public abstract class InternalExecutor<T> {
	protected static final Logger log = LoggerFactory.getLogger(InternalExecutor.class);

	public static final InternalExecutorResult RETRY = new InternalExecutorResult(true);

	public static final InternalExecutorResult PASS = new InternalExecutorResult(false);

	/**重试次数,默认3次*/
	int maxRetryCount = 3;

	/**间隔时间(单位:毫秒),默认500毫秒*/
	int retryTimes = 500;

	/**最大重试间隔时间(单位:毫秒),每次重试间隔时间取Math.min(retryTimes,maxRetryTimes)*/
	int maxRetryTimes = Integer.MAX_VALUE;

	/**
	 * 执行内容,如果需要循环执行抛出InternalExecutorException
	 * @return
	 * @throws InternalExecutorException
	 * @throws Exception 
	 */
	public abstract InternalExecutorResult<T> doRequest();

	public InternalExecutor() {
		super();
	}

	public InternalExecutor(int maxRetryCount, int retryTimes) {
		this.maxRetryCount = maxRetryCount;
		this.retryTimes = retryTimes;
	}

	public InternalExecutor(int maxRetryCount, int retryTimes, int maxRetryTimes) {
		this.maxRetryCount = maxRetryCount;
		this.retryTimes = retryTimes;
		this.maxRetryTimes = maxRetryTimes;
	}

	/**
	 * 启动执行器
	 * @return
	 * @throws
	 */
	public T run(){
		int sleepMillis = 0;
		for (int i = 1; i <= maxRetryCount+1; i++) {
			InternalExecutorResult<T> result = null;
			try {
				result = this.doRequest();
				if(result == null){
					result = PASS;
				}
				if(!result.needRetry){
					return result.getResult();
				}
			} catch (InternalExecutorException e) {
				log.error("error...",e);
			}

			if(i >= maxRetryCount+1){
				onFinishError();
				return result.getResult();
			}

			sleepMillis = Math.min(getNextSleepMillis(retryTimes,i,sleepMillis),maxRetryTimes);
			log.error("重试执行器第{}次执行发生异常,将于{}毫秒后重新发起.",i,sleepMillis);

			if(sleepMillis>0){
				try {
					Thread.sleep(sleepMillis);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 重试次数结束后还没有通过则会抛出一个异常
	 */
	public void onFinishError(){
		throw new GuarantyException("共重试"+maxRetryCount+"次,超出重试次数!");
	}


	/**
	 * 获取下一次重试时间,默认是retryTimes (毫秒)
	 * 可以配置成线行递增 例如 : retryTimes * (retryCount-1)
	 * @param retryTimes 配置的等待时间
	 * @param retryCount 重试次数从1开始
	 * @param lastSleepMillis 上一次等待执行时间
	 * @return 等待return时间后再次执行 (毫秒)
	 */
	public int getNextSleepMillis(int retryTimes,int retryCount,int lastSleepMillis){
		return retryTimes;
	}

	public static class InternalExecutorException extends RuntimeException {
		private static final long serialVersionUID = 2400444291334773579L;

		public InternalExecutorException() {
		}

		public InternalExecutorException(String message) {
			super(message);
		}

		public InternalExecutorException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	public static class InternalExecutorResult<R>{
		private R result;

		private boolean needRetry = false;

		public InternalExecutorResult() {
		}

		public InternalExecutorResult(R result) {
			this.result = result;
		}

		public InternalExecutorResult(R result, boolean needRetry) {
			this.result = result;
			this.needRetry = needRetry;
		}

		public InternalExecutorResult(boolean needRetry) {
			this.needRetry = needRetry;
		}

		public R getResult() {
			return result;
		}

		public void setResult(R result) {
			this.result = result;
		}
	}

	public static <R> R execute(InternalExecutor<R> executor){
		return executor.run();
	}

	public static <R> InternalExecutorResult<R> pass(R value){
		return new InternalExecutorResult(value,false);
	}

	public static <R> InternalExecutorResult<R> retry(R value){
		return new InternalExecutorResult(value,true);
	}
}