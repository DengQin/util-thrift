package com.dengqin.util.test;

import com.dengqin.util.manager.demo.MasterAndSlaveThriftManagerDemo;
import com.dengqin.util.pool.GenericConnectionProvider;

/**
 * Created by dq on 2018/1/5.
 */
public class MasterAndSlaveDemo {
	public static void main(String[] args) throws Exception {

		MasterAndSlaveThriftManagerDemo con = new MasterAndSlaveThriftManagerDemo();
		GenericConnectionProvider sl = new GenericConnectionProvider();
		sl.setServerIp("115.238.170.38");
		sl.setServerPort(28742);
		sl.setConTimeOut(6000);
		sl.afterPropertiesSet();

		GenericConnectionProvider ma = new GenericConnectionProvider();
		ma.setServerIp("115.238.170.38");
		ma.setServerPort(28741);
		ma.setConTimeOut(6000);
		ma.afterPropertiesSet();
		con.setMasterConnectionProvider(ma);
		con.setSlaveConnectionProvider(sl);

		con.querLongResult("");

		for (int i = 0; i < 10; i++) {
			try {
				Thread.sleep(10000L);
			} catch (Exception e) {
			}
			System.out.println(con.querLongResult(""));
		}
		System.out.println(con.querLongResult(""));
	}
}
