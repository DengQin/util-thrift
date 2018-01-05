package com.dengqin.util;

public class LongUtil {

	public static long parseLong(String str) {
		return parseLong(str, 0L);
	}

	public static long parseLong(String str, long defaultValue) {
		if (str == null) {
			return defaultValue;
		}
		try {
			return Long.parseLong(str);
		} catch (NumberFormatException nfe) {
			return defaultValue;
		}
	}

}
