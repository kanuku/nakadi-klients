package org.zalando.nakadi.client.java;

import java.util.Optional;

import org.zalando.nakadi.client.java.model.Cursor;

public class StreamParameters {
    private final Optional<Cursor> cursor;
    private final Optional<Integer> batchLimit;
    private final Optional<Integer> streamLimit;
    private final Optional<Integer> batchFlushTimeout;
    private final Optional<Integer> streamTimeout;
    private final Optional<Integer> streamKeepAliveLimit;
    private final Optional<String> flowId;

    public StreamParameters(Optional<Cursor> cursor, Optional<Integer> batchLimit, Optional<Integer> streamLimit,
            Optional<Integer> batchFlushTimeout, Optional<Integer> streamTimeout, Optional<Integer> streamKeepAliveLimit,
            Optional<String> flowId) {
        this.cursor = cursor;
        this.batchLimit = batchLimit;
        this.streamLimit = streamLimit;
        this.batchFlushTimeout = batchFlushTimeout;
        this.streamTimeout = streamTimeout;
        this.streamKeepAliveLimit = streamKeepAliveLimit;
        this.flowId = flowId;
    }

    public StreamParameters() {
        this.cursor = Optional.empty();
        this.batchLimit = Optional.empty();
        this.streamLimit = Optional.empty();
        this.batchFlushTimeout = Optional.empty();
        this.streamTimeout = Optional.empty();
        this.streamKeepAliveLimit = Optional.empty();
        this.flowId = Optional.empty();
    }

    public Optional<Cursor> getCursor() {
        return cursor;
    }

    public Optional<Integer> getBatchLimit() {
        return batchLimit;
    }

    public Optional<Integer> getStreamLimit() {
        return streamLimit;
    }

    public Optional<Integer> getBatchFlushTimeout() {
        return batchFlushTimeout;
    }

    public Optional<Integer> getStreamTimeout() {
        return streamTimeout;
    }

    public Optional<Integer> getStreamKeepAliveLimit() {
        return streamKeepAliveLimit;
    }

    public Optional<String> getFlowId() {
        return flowId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((batchFlushTimeout == null) ? 0 : batchFlushTimeout.hashCode());
        result = prime * result + ((batchLimit == null) ? 0 : batchLimit.hashCode());
        result = prime * result + ((cursor == null) ? 0 : cursor.hashCode());
        result = prime * result + ((flowId == null) ? 0 : flowId.hashCode());
        result = prime * result + ((streamKeepAliveLimit == null) ? 0 : streamKeepAliveLimit.hashCode());
        result = prime * result + ((streamLimit == null) ? 0 : streamLimit.hashCode());
        result = prime * result + ((streamTimeout == null) ? 0 : streamTimeout.hashCode());
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
        StreamParameters other = (StreamParameters) obj;
        if (batchFlushTimeout == null) {
            if (other.batchFlushTimeout != null)
                return false;
        } else if (!batchFlushTimeout.equals(other.batchFlushTimeout))
            return false;
        if (batchLimit == null) {
            if (other.batchLimit != null)
                return false;
        } else if (!batchLimit.equals(other.batchLimit))
            return false;
        if (cursor == null) {
            if (other.cursor != null)
                return false;
        } else if (!cursor.equals(other.cursor))
            return false;
        if (flowId == null) {
            if (other.flowId != null)
                return false;
        } else if (!flowId.equals(other.flowId))
            return false;
        if (streamKeepAliveLimit == null) {
            if (other.streamKeepAliveLimit != null)
                return false;
        } else if (!streamKeepAliveLimit.equals(other.streamKeepAliveLimit))
            return false;
        if (streamLimit == null) {
            if (other.streamLimit != null)
                return false;
        } else if (!streamLimit.equals(other.streamLimit))
            return false;
        if (streamTimeout == null) {
            if (other.streamTimeout != null)
                return false;
        } else if (!streamTimeout.equals(other.streamTimeout))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "StreamParameters [cursor=" + cursor + ", batchLimit=" + batchLimit + ", streamLimit=" + streamLimit
                + ", batchFlushTimeout=" + batchFlushTimeout + ", streamTimeout=" + streamTimeout + ", streamKeepAliveLimit="
                + streamKeepAliveLimit + ", flowId=" + flowId + "]";
    }
    
    

}
