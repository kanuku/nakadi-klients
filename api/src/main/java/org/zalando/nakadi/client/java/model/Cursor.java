package org.zalando.nakadi.client.java.model;

public class Cursor {
    private final Integer partition;
    private final Integer offset;

    /**
     * @param partition
     *            Id of the partition pointed to by this cursor.
     * @param offset
     *            Offset of the event being pointed to.
     */
    public Cursor(Integer partition, Integer offset) {
        
        
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
