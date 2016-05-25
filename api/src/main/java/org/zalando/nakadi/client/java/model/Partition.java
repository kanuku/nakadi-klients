package org.zalando.nakadi.client.java.model;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Partition information. Can be helpful when trying to start a stream using an
 * unmanaged API. This information is not related to the state of the consumer
 * clients.
 */
public class Partition {
    private final String partition;
    private final String oldestAvailableOffset;
    private final String newestAvailableOffset;

    /**
     * Partition information. Can be helpful when trying to start a stream using
     * an unmanaged API. This information is not related to the state of the
     * consumer clients.
     * 
     * @param partition
     *            The partition's id.
     * @param oldestAvailableOffset
     *            An offset of the oldest available Event in that partition.
     *            This value will be changing upon removal of Events from the
     *            partition by the background archiving/cleanup mechanism.
     * @param newestAvailableOffset
     *            An offset of the newest available Event in that partition.
     *            This value will be changing upon reception of new events for
     *            this partition by Nakadi. This value can be used to construct
     *            a cursor when opening streams (see `GET
     *            /event-type/{name}/events` for details). Might assume the
     *            special name BEGIN, meaning a pointer to the offset of the
     *            oldest available event in the partition.
     */
    @JsonCreator
    public Partition(String partition, String oldestAvailableOffset, String newestAvailableOffset) {
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

}
