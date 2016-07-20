package org.zalando.nakadi.client.java.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Partition information. Can be helpful when trying to start a stream using an unmanaged API. This information is not
 * related to the state of the consumer clients.
 */
public class Partition {
    private final String partition;
    private final String oldestAvailableOffset;
    private final String newestAvailableOffset;

    /**
     * Partition information. Can be helpful when trying to start a stream using an unmanaged API. This information is
     * not related to the state of the consumer clients.
     * 
     * @param partition
     *            The partition's id.
     * @param oldestAvailableOffset
     *            An offset of the oldest available Event in that partition.
     * @param newestAvailableOffset
     *            An offset of the newest available Event in that partition.
     */
    @JsonCreator
    public Partition(@JsonProperty("partition") String partition, @JsonProperty("oldest_available_offset") String oldestAvailableOffset,
            @JsonProperty("newest_available_offset") String newestAvailableOffset) {
        this.partition = partition;
        this.oldestAvailableOffset = oldestAvailableOffset;
        this.newestAvailableOffset = newestAvailableOffset;
    }

    public String getPartition() {
        return partition;
    }

    public String getOldestAvailableOffset() {
        return oldestAvailableOffset;
    }

    public String getNewestAvailableOffset() {
        return newestAvailableOffset;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((newestAvailableOffset == null) ? 0 : newestAvailableOffset.hashCode());
        result = prime * result + ((oldestAvailableOffset == null) ? 0 : oldestAvailableOffset.hashCode());
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
        Partition other = (Partition) obj;
        if (newestAvailableOffset == null) {
            if (other.newestAvailableOffset != null)
                return false;
        } else if (!newestAvailableOffset.equals(other.newestAvailableOffset))
            return false;
        if (oldestAvailableOffset == null) {
            if (other.oldestAvailableOffset != null)
                return false;
        } else if (!oldestAvailableOffset.equals(other.oldestAvailableOffset))
            return false;
        if (partition == null) {
            if (other.partition != null)
                return false;
        } else if (!partition.equals(other.partition))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Partition [partition=" + partition + ", oldestAvailableOffset=" + oldestAvailableOffset + ", newestAvailableOffset=" + newestAvailableOffset + "]";
    }

}
