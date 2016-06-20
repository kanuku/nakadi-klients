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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((offset == null) ? 0 : offset.hashCode());
        result = prime * result + ((partition == null) ? 0 : partition.hashCode());
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
        Cursor other = (Cursor) obj;
        if (offset == null) {
            if (other.offset != null)
                return false;
        } else if (!offset.equals(other.offset))
            return false;
        if (partition == null) {
            if (other.partition != null)
                return false;
        } else if (!partition.equals(other.partition))
            return false;
        return true;
    }

    
    
}
