package org.zalando.nakadi.client.java.model;

import java.util.List;

import com.fasterxml.jackson.annotation.*;

/**
 * One chunk of events in a stream. A batch consists of an array of `Event`s
 * plus a `Cursor` pointing to the offset of the last Event in the stream. The
 * size of the array of Event is limited by the parameters used to initialize a
 * Stream. If acting as a keep alive message (see `GET
 * /event-type/{name}/events`) the events array will be omitted. Sequential
 * batches might repeat the cursor if no new events arrive.
 */

public class EventStreamBatch<T extends Event> {
    private final Cursor cursor;
    private final List<T> events;

    /**
     * One chunk of events in a stream. A batch consists of an array of `Event`s
     * plus a `Cursor` pointing to the offset of the last Event in the stream.
     * The size of the array of Event is limited by the parameters used to
     * initialize a Stream. If acting as a keep alive message (see `GET
     * /event-type/{name}/events`) the events array will be omitted. Sequential
     * batches might repeat the cursor if no new events arrive.
     * 
     * @param cursor
     *            The cursor point to an event in the stream.
     * @param events
     *            The Event definition will be externalized in future versions
     *            of this document. A basic payload of an Event. The actual
     *            schema is dependent on the information configured for the
     *            EventType, as is its enforcement (see POST /event-types).
     *            Setting of metadata properties are dependent on the configured
     *            enrichment as well. For explanation on default configurations
     *            of validation and enrichment, see documentation of
     *            `EventType.type`. For concrete examples of what will be
     *            enforced by Nakadi see the objects sEvent and DataChangeEvent
     *            below.
     */

    @JsonCreator
    public EventStreamBatch(
            @JsonProperty("cursor") Cursor cursor,
            @JsonProperty("events") List<T> events
            ) {
        super();
        this.cursor = cursor;
        this.events = events;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public List<T> getEvents() {
        return events;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cursor == null) ? 0 : cursor.hashCode());
        result = prime * result + ((events == null) ? 0 : events.hashCode());
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
        EventStreamBatch other = (EventStreamBatch) obj;
        if (cursor == null) {
            if (other.cursor != null)
                return false;
        } else if (!cursor.equals(other.cursor))
            return false;
        if (events == null) {
            if (other.events != null)
                return false;
        } else if (!events.equals(other.events))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "EventStreamBatch [cursor=" + cursor + ", events=" + events + "]";
    }
    
    

}
