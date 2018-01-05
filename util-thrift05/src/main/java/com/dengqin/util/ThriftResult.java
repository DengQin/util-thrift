package com.dengqin.util;

/**
 * 基础的主备结果
 */
public class ThriftResult<T> {

	/**
	 * 结果来源
	 */
	private ResultSource resultSource;

	/**
	 * 结果
	 */
	private T result;

	public ThriftResult(ResultSource resultSource, T result) {
		super();
		this.resultSource = resultSource;
		this.result = result;
	}

	public ResultSource getResultSource() {
		return resultSource;
	}

	public void setResultSource(ResultSource resultSource) {
		this.resultSource = resultSource;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "ThriftResult [resultSource=" + resultSource + ", result=" + result + "]";
	}

}
