/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dengqin.util.pool.validator.impl;

import com.dengqin.util.pool.validator.BaseValidator;
import org.apache.thrift.transport.TSocket;

/**
 * 通用验证器
 */
public class CommonTSocketValidator implements BaseValidator {

	@Override
	public boolean isValid(Object obj) {
		try {
			if (obj instanceof TSocket) {
				TSocket thriftSocket = (TSocket) obj;
				if (thriftSocket.isOpen()) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

}
