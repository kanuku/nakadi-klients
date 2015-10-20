package de.zalando.nakadi.client;


import de.zalando.nakadi.client.domain.Cursor;
import de.zalando.nakadi.client.domain.Event;

public interface EventListener {
    void onReceive(final Cursor cursor, final Event event);
}
