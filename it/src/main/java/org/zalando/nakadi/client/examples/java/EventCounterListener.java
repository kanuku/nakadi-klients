package org.zalando.nakadi.client.examples.java;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.nakadi.client.java.ClientError;
import org.zalando.nakadi.client.java.model.Cursor;

public class EventCounterListener implements org.zalando.nakadi.client.java.Listener<MeetingsEvent> {
    private final String id;
    private AtomicLong eventCount = new AtomicLong(0);
    private Logger log = LoggerFactory.getLogger(this.getClass());

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
        log.info("#####################################");
        log.info("Received " + events.size());
        log.info(String.format("Has a total of %d events", eventCount.get()));
        log.info("#####################################");

    }

    @Override
    public void onError(String eventUrl, java.util.Optional<ClientError> error) {
        if (error.isPresent()) {
            ClientError clientError = error.get();
            log.error("An error occurred" + clientError.getMsg());
        }

    }
}