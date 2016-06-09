package org.zalando.nakadi.client.java.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EventTypeStatistics {
    private final Integer messagesPerMinute;
    private final Integer messageSize;
    private final Integer readParallelism;
    private final Integer writeParallelism;
    
    
    @JsonCreator
    public EventTypeStatistics(
    		 @JsonProperty("messages_per_minute") Integer messagesPerMinute, 
    		 @JsonProperty("message_size") Integer messageSize, 
    		 @JsonProperty("read_parallelism") Integer readParallelism, 
    		 @JsonProperty("write_parallelism") Integer writeParallelism) {
        this.messagesPerMinute = messagesPerMinute;
        this.messageSize = messageSize;
        this.readParallelism = readParallelism;
        this.writeParallelism = writeParallelism;
    }
    public Integer getMessagesPerMinute() {
        return messagesPerMinute;
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
