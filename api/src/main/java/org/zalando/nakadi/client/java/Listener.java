package org.zalando.nakadi.client.java;

import java.util.List;

import org.zalando.nakadi.client.java.model.Cursor;
import org.zalando.nakadi.client.java.model.Event;

public interface Listener<T extends Event> {
    String getId();

    void onReceive(String eventUrl, Cursor cursor, List<T> events);

    void onError(String eventUrl, Cursor cursor, ClientError error);
}
