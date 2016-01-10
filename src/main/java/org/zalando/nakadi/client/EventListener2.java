package org.zalando.nakadi.client;

public interface EventListener2 extends Listener {
    void onConnectionClosed(final String topic, final String partitionId);
    void onConnectionClosed(final String topic, final String partitionId, final Exception cause);
}
