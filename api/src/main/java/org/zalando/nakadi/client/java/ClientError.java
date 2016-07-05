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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((msg == null) ? 0 : msg.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClientError other = (ClientError) obj;
        if (msg == null) {
            if (other.msg != null)
                return false;
        } else if (!msg.equals(other.msg))
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ClientError [msg=" + msg + ", status=" + status + ", exception=" + exception + "]";
    }
	
	

}