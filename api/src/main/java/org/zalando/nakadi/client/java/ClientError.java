package org.zalando.nakadi.client.java;

import java.util.Optional;

public class ClientError {
	private final String msg;
	private final Optional<Integer> status;

	public ClientError(String msg, Optional<Integer> status) {
		this.msg = msg;
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public Optional<Integer> getStatus() {
		return status;
	}

}