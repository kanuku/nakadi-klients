package org.zalando.nakadi.client.java;

public class StreamParameters {
    private final Integer partition;
    private final Integer offset;

    public StreamParameters(Integer partition, Integer offset) {
        this.partition = partition;
        this.offset = offset;
    }

    public Integer getPartition() {
        return partition;
    }

    public Integer getOffset() {
        return offset;
    }

}
