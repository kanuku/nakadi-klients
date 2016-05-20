package org.zalando.nakadi.client.java;

import java.util.Optional;

public class ClientError {
	private final String msg;
	private final Optional<Integer> status;
	private final Optional<Throwable> exception;

	public ClientError(String msg, Optional<Integer> status,
			Optional<Throwable> exception) {
		this.msg = msg;
		this.status = status;
		this.exception = exception;
	}

	public ClientError(String msg) {
		this.msg = msg;
		this.status = Optional.empty();
		this.exception = Optional.empty();
	}

	public String getMsg() {
		return msg;
	}

	public Optional<Integer> getStatus() {
		return status;
	}

	public Optional<Throwable> getException() {
		return exception;
	}

}