package com.dengqin.util.pool;

import org.apache.thrift.transport.TSocket;

/**
 * 链接提供者
 */
public interface ConnectionProvider {
	/**
	 * 取链接池中的一个链接
	 * 
	 * @return TSocket链接
	 */
	public TSocket getConnection();

	/**
	 * 归还链接
	 * 
	 * @param socket
	 */
	public void returnCon(TSocket socket);

	/**
	 * 丢弃链接
	 * 
	 * @param socket
	 */
	public void dropBrokenCon(TSocket socket);

	/**
	 * 输出池当前使用情况
	 */
	public void logPoolInfo();
}