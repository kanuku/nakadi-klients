package org.zalando.nakadi.client.java;

public class ClientError {
	private final String msg;
	private final Integer status;

	public ClientError(String msg, Integer status) {
		this.msg = msg;
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public Integer getStatus() {
		return status;
	}

}