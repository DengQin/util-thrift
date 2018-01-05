package com.dengqin.util.pool;

import com.dengqin.util.pool.validator.BaseValidator;
import com.dengqin.util.pool.validator.impl.CommonTSocketValidator;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thrift池工厂
 */
public class ThriftPoolableObjectFactory implements PoolableObjectFactory {

	/** 日志记录器 */
	public static final Logger logger = LoggerFactory.getLogger(ThriftPoolableObjectFactory.class);
	/** 服务的IP */
	private String serverIp;
	/** 服务的端口 */
	private int serverPort;
	/** 超时设置 */
	private int timeOut;

	private BaseValidator validator = new CommonTSocketValidator();

	public void setValidator(BaseValidator validator) {
		this.validator = validator;
	}

	/**
	 * @param serverIp
	 * @param serverPort
	 * @param timeOut
	 */
	public ThriftPoolableObjectFactory(String serverIp, int serverPort, int timeOut) {
		this.serverIp = serverIp;
		this.serverPort = serverPort;
		this.timeOut = timeOut;
	}

	@Override
	public void destroyObject(Object arg0) throws Exception {
		if (arg0 == null) {
			return;
		}
		if (arg0 instanceof TSocket) {
			TSocket socket = (TSocket) arg0;
			if (socket.isOpen()) {
				socket.close();
			}
		}
	}

	@Override
	public Object makeObject() throws Exception {
		TTransport transport = new TSocket(this.serverIp, this.serverPort, this.timeOut);
		transport.open();
		return transport;
	}

	@Override
	public boolean validateObject(Object arg0) {
		return validator.isValid(arg0);
	}

	@Override
	public void passivateObject(Object arg0) throws Exception {
		// DO NOTHING
	}

	@Override
	public void activateObject(Object arg0) throws Exception {
		// DO NOTHING
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

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

}