package com.dengqin.util.test;

import com.dengqin.util.manager.demo.OneThriftManagerDemo;
import com.dengqin.util.pool.GenericConnectionProvider;

/**
 * Created by dq on 2018/1/5.
 */
public class OneThriftDemo {
	public static void main(String[] args) throws Exception {

		OneThriftManagerDemo con = new OneThriftManagerDemo();
		GenericConnectionProvider gen = new GenericConnectionProvider();
		gen.setServerIp("58.215.138.250");
		gen.setServerPort(12300);
		gen.setConTimeOut(6000);
		gen.afterPropertiesSet();
		con.setConnectionProvider(gen);

		con.getRes(1, 1, "");

	}
}
