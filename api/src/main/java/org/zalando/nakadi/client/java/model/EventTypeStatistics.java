package org.zalando.nakadi.client.java.model;

public class EventTypeStatistics {
    private final Integer expectedWriteRate;
    private final Integer messageSize;
    private final Integer readParallelism;
    private final Integer writeParallelism;
    
    
    
    public EventTypeStatistics(Integer expectedWriteRate, Integer messageSize, Integer readParallelism, Integer writeParallelism) {
        this.expectedWriteRate = expectedWriteRate;
        this.messageSize = messageSize;
        this.readParallelism = readParallelism;
        this.writeParallelism = writeParallelism;
    }
    public Integer getExpectedWriteRate() {
        return expectedWriteRate;
    }
    public Integer getMessageSize() {
        return messageSize;
    }
    public Integer getReadParallelism() {
        return readParallelism;
    }
    public Integer getWriteParallelism() {
        return writeParallelism;
    }
    
    
    
}
