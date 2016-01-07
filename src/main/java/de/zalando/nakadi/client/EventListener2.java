package de.zalando.nakadi.client;


import de.zalando.nakadi.client.domain.Cursor;
import de.zalando.nakadi.client.domain.Event;

public interface EventListener2 extends EventListener {
    void onConnectionClosed(final String topic, final String partitionId);
    void onConnectionClosed(final String topic, final String partitionId, final Exception cause);
}
