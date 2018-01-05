package com.dengqin.util.manager.demo;

import com.dengqin.util.Constant;
import com.dengqin.util.LongUtil;
import com.dengqin.util.ResultSource;
import com.dengqin.util.ThriftResult;
import com.dengqin.util.manager.BaseThriftManager;
import com.dengqin.util.pool.ConnectionProvider;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 提供主备设置的thrift
 * 
 * Created by dq on 2018/1/5.
 */
public class MasterAndSlaveThriftManagerDemo {

	private static final Logger log = LoggerFactory.getLogger(MasterAndSlaveThriftManagerDemo.class);

	/** 连接提供池 */
	private ConnectionProvider masterConnectionProvider;

	/** 连接提供池 */
	private ConnectionProvider slaveConnectionProvider;

	/** 重试次数 */
	private int retryTimes = Constant.DEFAULT_RETRY_TIMES;

	/** 使用备服务的时间毫秒数 */
	private long useSlaveMs = Constant.DEFAULT_USER_SLAVE_MS;

	public ThriftResult<Long> querLongResult(final String name) {
		BaseThriftManager<Long> thrift = new BaseThriftManager<Long>(masterConnectionProvider, slaveConnectionProvider,
				new ThriftResult<Long>(ResultSource.FAIL, 0L), retryTimes, useSlaveMs) {
			@Override
			protected String getErrorMsg() {
				return "querLongResult失败,passport[" + name + "]";
			}

			@Override
			protected ThriftResult<Long> callThrift(ResultSource resultSource, TSocket socket) throws Exception {
				TFramedTransport tFramedTransport = new TFramedTransport(socket);
				TBinaryProtocol protocol = new TBinaryProtocol(tFramedTransport);
				protocol.setReadLength(maxReadLength);
				// userinfo_service.Client client = new
				// userinfo_service.Client(protocol);
				// String res = client.lg_userinfo_transPassport(name);
				return new ThriftResult<Long>(resultSource, LongUtil.parseLong("res"));
			}

		};
		return thrift.run();
	}

	public ThriftResult<String> queryStringResult(final long name) {
		BaseThriftManager<String> thrift = new BaseThriftManager<String>(masterConnectionProvider,
				slaveConnectionProvider, new ThriftResult<String>(ResultSource.FAIL, ""), retryTimes, useSlaveMs) {
			@Override
			protected String getErrorMsg() {
				return "queryStringResult失败,name[" + name + "]";
			}

			@Override
			protected ThriftResult<String> callThrift(ResultSource resultSource, TSocket socket) throws Exception {
				TFramedTransport tFramedTransport = new TFramedTransport(socket);
				TBinaryProtocol protocol = new TBinaryProtocol(tFramedTransport);
				protocol.setReadLength(maxReadLength);
				// userinfo_service.Client client = new
				// userinfo_service.Client(protocol);
				// String result =
				// client.lg_userinfo_trans(Long.toString(name));

				return new ThriftResult<String>(resultSource, "result");
			}
		};
		return thrift.run();
	}

	public void setMasterConnectionProvider(ConnectionProvider masterConnectionProvider) {
		this.masterConnectionProvider = masterConnectionProvider;
	}

	public void setSlaveConnectionProvider(ConnectionProvider slaveConnectionProvider) {
		this.slaveConnectionProvider = slaveConnectionProvider;
	}

	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}

	public void setUseSlaveMs(long useSlaveMs) {
		this.useSlaveMs = useSlaveMs;
	}

}
