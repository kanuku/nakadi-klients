package org.zalando.nakadi.client.examples.java;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.zalando.nakadi.client.java.ClientError;
import org.zalando.nakadi.client.java.model.Cursor;

public class EventCounterListener implements org.zalando.nakadi.client.java.Listener<MeetingsEvent> {
    private final String id;
    private AtomicLong eventCount = new AtomicLong(0);

    public EventCounterListener(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void onReceive(String eventUrl, Cursor cursor, List<MeetingsEvent> events) {
        eventCount.addAndGet(events.size());
        System.out.println("#####################################");
        System.out.println("Received " + events.size());
        System.out.println(String.format("Has a total of %d events", eventCount.get()));
        System.out.println("#####################################");

    }

    @Override
    public void onError(String eventUrl, java.util.Optional<ClientError> error) {
        // TODO Auto-generated method stub

    }

}