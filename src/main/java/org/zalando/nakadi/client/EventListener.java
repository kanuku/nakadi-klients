package org.zalando.nakadi.client;


import org.zalando.nakadi.client.domain.Cursor;
import org.zalando.nakadi.client.domain.Event;

/**
 * @deprecated use  {@link EventListener2} instead
 */
@Deprecated
public interface EventListener {
    void onReceive(final Cursor cursor, final Event event);
}
