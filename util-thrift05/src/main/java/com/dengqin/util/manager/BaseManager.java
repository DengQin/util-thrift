package com.dengqin.util.manager;

import com.dengqin.util.ThriftResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 基础管理器，核心为实现主备切换
 * 
 * @param <T>
 */
public abstract class BaseManager<T> {

	private static final Logger log = LoggerFactory.getLogger(BaseManager.class);

	private final static AtomicInteger counter = new AtomicInteger();

	private final static AtomicBoolean useMaster = new AtomicBoolean(true);

	private final static AtomicLong lastCheckMasterTimeStamp = new AtomicLong(0L);

	/**
	 * 主服务失败尝试次数
	 */
	private static final int DEFAULT_RETRY_TIMES = 3;

	/**
	 * 默认使用从服务的时间,10分钟，单位：毫秒
	 */
	private static final long DEFAULT_USER_SLAVE_MS = 600 * 1000L;

	protected ThriftResult<T> defaultResult;

	private int retryTimes;

	/**
	 * 使用备服务的时间毫秒数
	 */
	private long useSlaveMs;

	public BaseManager(ThriftResult<T> defaultResult) {
		super();
		this.defaultResult = defaultResult;
	}

	public BaseManager(ThriftResult<T> defaultResult, int retryTimes, long useSlaveMs) {
		super();
		this.defaultResult = defaultResult;
		this.retryTimes = retryTimes < 1 ? DEFAULT_RETRY_TIMES : retryTimes;
		this.useSlaveMs = useSlaveMs < 1 ? DEFAULT_USER_SLAVE_MS : useSlaveMs;
	}

	/**
	 * 执行服务，自动主备切换
	 * 
	 * @return ThriftResult<T>
	 */
	public ThriftResult<T> run() {
		if (useMaster.get()) {
			// 优先使用主服务，主服务挂了，使用备服务，超过次数，切到备服务
			try {
				// 使用主服务
				ThriftResult<T> r = runWithMaster();
				counter.set(0);
				return r;
			} catch (Exception e) {
				logError(e);
			}
			try {
				// 使用备服务
				ThriftResult<T> r = runWithSlave();
				// 备服务正常
				int num = counter.incrementAndGet();
				if (num > retryTimes) {
					useMaster.set(false);
					lastCheckMasterTimeStamp.set(System.currentTimeMillis());
				}
				return r;
			} catch (Exception e) {
				logError(e);
			}
		} else {
			// 优先使用备服务，时间段外检查主服务是否好了，好了切回主服务；备服务挂了，尝试主服务
			if (System.currentTimeMillis() - lastCheckMasterTimeStamp.get() > useSlaveMs) {
				// 时间超过，尝试使用主服务
				try {
					// 使用主服务
					ThriftResult<T> r = runWithMaster();
					counter.set(0);
					useMaster.set(true);
					return r;
				} catch (Exception e) {
					// 推迟到下个时间点
					lastCheckMasterTimeStamp.addAndGet(useSlaveMs);
					logError(e);
				}
			}
			try {
				// 使用备服务
				return runWithSlave();
			} catch (Exception e) {
				logError(e);
			}
			try {
				// 使用主服务
				ThriftResult<T> r = runWithMaster();
				counter.set(0);
				useMaster.set(true);
				return r;
			} catch (Exception e) {
			}
		}
		return defaultResult;
	}

	protected abstract String getErrorMsg();

	protected abstract ThriftResult<T> runWithMaster() throws Exception;

	protected abstract ThriftResult<T> runWithSlave() throws Exception;

	/**
	 * 只打印堆栈的前3行
	 */
	private void logError(Exception e) {
		String stackMsg1 = "";
		String stackMsg2 = "";
		String stackMsg3 = "";
		StackTraceElement[] stackTrace = e.getStackTrace();
		if (stackTrace.length > 0) {
			stackMsg1 = stackTrace[0].toString();
		}
		if (stackTrace.length > 1) {
			stackMsg2 = stackTrace[1].toString();
		}
		if (stackTrace.length > 2) {
			stackMsg3 = stackTrace[2].toString();
		}
		log.error(
				getErrorMsg() + "\n\t" + e.getMessage() + "\n\t" + stackMsg1 + "\n\t" + stackMsg2 + "\n\t" + stackMsg3);
	}
}
