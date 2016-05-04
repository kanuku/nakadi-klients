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

}
