package org.zalando.nakadi.client.java.model;

/**
 * Partition information. Can be helpful when trying to start a stream using an
 * unmanaged API. This information is not related to the state of the consumer
 * clients.
 */
public class Partition {
    private final Integer partition;
    private final Integer oldestAvailableOffset;
    private final Integer newestAvailableOffset;

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
    public Partition(Integer partition, Integer oldestAvailableOffset, Integer newestAvailableOffset) {
        this.partition = partition;
        this.oldestAvailableOffset = oldestAvailableOffset;
        this.newestAvailableOffset = newestAvailableOffset;
    }

    public Integer getPartition() {
        return partition;
    }

    public Integer getOldestAvailableOffset() {
        return oldestAvailableOffset;
    }

    public Integer getNewestAvailableOffset() {
        return newestAvailableOffset;
    }

}
