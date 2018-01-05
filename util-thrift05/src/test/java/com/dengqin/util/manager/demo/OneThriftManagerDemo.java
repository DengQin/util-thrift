package com.dengqin.util.manager.demo;

import com.dengqin.util.pool.ConnectionProvider;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dq on 2018/1/5.
 */
public class OneThriftManagerDemo {

	private static final Logger log = LoggerFactory.getLogger(OneThriftManagerDemo.class);

	private static final int readLength = 10240;

	/** 连接提供池 */
	private ConnectionProvider connectionProvider;

	public String getRes(long name, long timestamp, String sign) {
		return getRes(name, timestamp, sign, true);
	}

	/**
	 * ping
	 */
	public boolean ping() {
		TSocket socket = null;
		try {
			socket = connectionProvider.getConnection();
			TFramedTransport tFramedTransport = new TFramedTransport(socket);
			TBinaryProtocol protocol = new TBinaryProtocol(tFramedTransport);
			protocol.setReadLength(readLength);

			return true;
		} catch (Exception e) {
			return false;
		} finally {
			connectionProvider.returnCon(socket);
		}
	}

	private String getRes(long name, long timestamp, String sign, boolean tryAgain) {
		TSocket socket = null;
		try {
			socket = connectionProvider.getConnection();
			TFramedTransport tFramedTransport = new TFramedTransport(socket);
			TBinaryProtocol protocol = new TBinaryProtocol(tFramedTransport);
			protocol.setReadLength(readLength);
			// 调用服务执行结果result
			String result = "";
			return result;
		} catch (Exception e) {
			log.error("getRes失败,name[" + name + "]" + e.getMessage(), e);
			closeTSocket(socket);
			socket = null;
			if (tryAgain) {
				return getRes(name, timestamp, sign, false);
			} else {
				return null;
			}
		} finally {
			connectionProvider.returnCon(socket);
		}
	}

	private void closeTSocket(TSocket socket) {
		try {
			if (socket != null) {
				connectionProvider.dropBrokenCon(socket);
				socket.close();
			}
		} catch (Exception e) {
			log.error("closeTSocket关闭TSocket出错" + e.getMessage(), e);
		}
	}

	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

}
