package org.zalando.nakadi.client.java.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Cursor {
    private final String partition;
    private final String offset;

    /**
     * @param partition
     *            Id of the partition pointed to by this cursor.
     * @param offset
     *            Offset of the event being pointed to.
     */
    @JsonCreator
    public Cursor(
            @JsonProperty("partition") 
            String partition, 
            @JsonProperty("offset") 
            String offset) {
        
        
        this.partition = partition;
        this.offset = offset;
    }

    public String getPartition() {
        return partition;
    }

    public String getOffset() {
        return offset;
    }

}
