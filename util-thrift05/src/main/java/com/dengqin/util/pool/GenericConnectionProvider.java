package com.dengqin.util.pool;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.thrift.transport.TSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class GenericConnectionProvider implements ConnectionProvider, InitializingBean, DisposableBean {

	private static Logger log = LoggerFactory.getLogger(GenericConnectionProvider.class);
	/**
	 * 服务的IP地址
	 */
	private String serverIp;
	/**
	 * 服务的端口
	 */
	int serverPort;
	/**
	 * 连接超时配置
	 */
	int conTimeOut;
	/**
	 * 可以从缓存池中分配对象的最大数量
	 */
	int maxActive = GenericObjectPool.DEFAULT_MAX_ACTIVE;
	/**
	 * 缓存池中最大空闲对象数量
	 */
	int maxIdle = GenericObjectPool.DEFAULT_MAX_IDLE;
	/**
	 * 缓存池中最小空闲对象数量
	 */
	int minIdle = GenericObjectPool.DEFAULT_MIN_IDLE;
	/**
	 * 阻塞的最大数量
	 */
	long maxWait = GenericObjectPool.DEFAULT_MAX_WAIT;
	/**
	 * 参数timeBetweenEvictionRunsMillis，设定间隔每过多少毫秒进行一次后台对象清理的行动。如果这个值不是正数，则实际上不会进行后台对象清理。
	 */
	long timeBetweenEvictionRunsMillis = 30 * 1000;

	/**
	 * 从缓存池中分配对象，是否执行PoolableObjectFactory.validateObject方法
	 */
	boolean testOnBorrow = GenericObjectPool.DEFAULT_TEST_ON_BORROW;
	boolean testOnReturn = GenericObjectPool.DEFAULT_TEST_ON_RETURN;
	boolean testWhileIdle = GenericObjectPool.DEFAULT_TEST_WHILE_IDLE;
	/**
	 * 对象缓存池
	 */
	GenericObjectPool genericObjectPool = null;

	private ThriftPoolableObjectFactory thriftPoolableObjectFactory = null;

	/**
	 *
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		// 设置factory
		if (thriftPoolableObjectFactory == null) {
			thriftPoolableObjectFactory = new ThriftPoolableObjectFactory(serverIp, serverPort, conTimeOut);
		}
		// 对象池
		genericObjectPool = new GenericObjectPool(thriftPoolableObjectFactory);

		genericObjectPool.setMaxActive(maxActive);
		genericObjectPool.setMaxIdle(maxIdle);
		genericObjectPool.setMinIdle(minIdle);
		genericObjectPool.setMaxWait(maxWait);
		genericObjectPool.setTestOnBorrow(testOnBorrow);
		genericObjectPool.setTestOnReturn(testOnReturn);
		genericObjectPool.setTestWhileIdle(testWhileIdle);

		genericObjectPool.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		genericObjectPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);

		StringBuilder sb = new StringBuilder();
		sb.append("afterPropertiesSet create GenericObjectPool,host=").append(serverIp).append(",port=")
				.append(serverPort).append(",timeOut=").append(conTimeOut).append(",maxActive=").append(maxActive)
				.append(",maxIdle=").append(maxIdle).append(",maxWait=").append(maxWait).append(",isTestOnBorrow=")
				.append(testOnBorrow).append(",isTestOnReturn=").append(testOnReturn).append(",isTestWhileIdle=")
				.append(testWhileIdle);
		log.info(sb.toString());
	}

	@Override
	public void destroy() {
		try {
			genericObjectPool.close();
		} catch (Exception e) {
			throw new RuntimeException("erorr destroy():" + e.getMessage(), e);
		}
	}

	@Override
	public TSocket getConnection() {
		try {
			TSocket socket = (TSocket) genericObjectPool.borrowObject();
			return socket;
		} catch (Exception e) {
			throw new RuntimeException("error getConnection() : " + e.getMessage(), e);
		}
	}

	public void dropBrokenCon(TSocket socket) {
		if (socket != null) {
			try {
				genericObjectPool.invalidateObject(socket);
			} catch (Exception e) {
				log.error("error dropBrokenCon :" + e.getMessage(), e);
			}
		}
	}

	@Override
	public void returnCon(TSocket socket) {
		try {
			if (socket != null) {
				genericObjectPool.returnObject(socket);
			}
		} catch (Exception e) {
			throw new RuntimeException("error returnCon()：" + e.getMessage(), e);
		}
	}

	public void logPoolInfo() {

		log.info("maxActive:{},maxIdle:{},curActive:{},curIdle:{}", new Object[] { genericObjectPool.getMaxActive(),
				genericObjectPool.getMaxIdle(), genericObjectPool.getNumActive(), genericObjectPool.getNumIdle() });
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public int getConTimeOut() {
		return conTimeOut;
	}

	public void setConTimeOut(int conTimeOut) {
		this.conTimeOut = conTimeOut;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public long getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(long maxWait) {
		this.maxWait = maxWait;
	}

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public boolean isTestOnReturn() {
		return testOnReturn;
	}

	public void setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	public boolean isTestWhileIdle() {
		return testWhileIdle;
	}

	public void setTestWhileIdle(boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	public GenericObjectPool getGenericObjectPool() {
		return genericObjectPool;
	}

	public void setGenericObjectPool(GenericObjectPool genericObjectPool) {
		this.genericObjectPool = genericObjectPool;
	}

	/**
	 * @return the thriftPoolableObjectFactory
	 */
	public ThriftPoolableObjectFactory getThriftPoolableObjectFactory() {
		return thriftPoolableObjectFactory;
	}

	/**
	 * @param thriftPoolableObjectFactory
	 *            the thriftPoolableObjectFactory to set
	 */
	public void setThriftPoolableObjectFactory(ThriftPoolableObjectFactory thriftPoolableObjectFactory) {
		this.thriftPoolableObjectFactory = thriftPoolableObjectFactory;
	}
}