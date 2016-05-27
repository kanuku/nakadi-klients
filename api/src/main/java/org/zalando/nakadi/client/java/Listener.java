package org.zalando.nakadi.client.java;

import java.util.List;
import java.util.Optional;

import org.zalando.nakadi.client.java.model.Cursor;
import org.zalando.nakadi.client.java.model.Event;

public interface Listener<T extends Event> {
	
	String getId();

	void onReceive(String eventType, Cursor cursor, List<T> events);

	void onSubscribed(String eventType, Optional<Cursor> cursor);

	void onError(String eventType, Optional<ClientError> error);
}
