package com.dengqin.util.manager;

import com.dengqin.util.ResultSource;
import com.dengqin.util.ThriftResult;
import com.dengqin.util.pool.ConnectionProvider;
import org.apache.thrift.transport.TSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thrift的基础管理器，对主备连接池进行管理调用
 * 
 * @param <T>
 */
public abstract class BaseThriftManager<T> extends BaseManager<T> {

	private static final Logger log = LoggerFactory.getLogger(BaseThriftManager.class);

	protected final int maxReadLength = 1024;

	/** 主服务连接池 */
	private ConnectionProvider masterConnectionProvider;

	/** 备服务连接池 */
	private ConnectionProvider slaveConnectionProvider;

	ThriftResult<T> defaultResult;

	public BaseThriftManager(ConnectionProvider masterConnectionProvider, ConnectionProvider slaveConnectionProvider,
			ThriftResult<T> defaultResult, int retryTimes, long useSlaveMs) {
		super(defaultResult, retryTimes, useSlaveMs);
		this.masterConnectionProvider = masterConnectionProvider;
		this.slaveConnectionProvider = slaveConnectionProvider;
	}

	protected abstract ThriftResult<T> callThrift(ResultSource resultSource, TSocket socket) throws Exception;

	protected ThriftResult<T> runWithMaster() throws Exception {
		TSocket socket = null;
		try {
			socket = masterConnectionProvider.getConnection();
			return callThrift(ResultSource.MASTER, socket);
		} catch (Exception e) {
			dropBrokenCon(masterConnectionProvider, socket);
			socket = null;
			throw e;
		} finally {
			masterConnectionProvider.returnCon(socket);
		}
	}

	protected ThriftResult<T> runWithSlave() throws Exception {
		TSocket socket = null;
		try {
			socket = slaveConnectionProvider.getConnection();
			return callThrift(ResultSource.SLAVE, socket);
		} catch (Exception e) {
			dropBrokenCon(slaveConnectionProvider, socket);
			socket = null;
			throw e;
		} finally {
			slaveConnectionProvider.returnCon(socket);
		}
	}

	private void dropBrokenCon(ConnectionProvider connectionProvider, TSocket socket) {
		try {
			if (socket != null) {
				connectionProvider.dropBrokenCon(socket);
				socket.close();
			}
		} catch (Exception e) {
			log.error("关闭TSocket出错" + e.getMessage(), e);
		}
	}

}
