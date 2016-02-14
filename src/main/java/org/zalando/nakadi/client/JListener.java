package org.zalando.nakadi.client;


import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import scala.collection.JavaConversions;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

public interface JListener {
    String id();
    void onReceive(String topic, String partition, Cursor cursor, JEvent event);
    void onConnectionOpened(String topic, String partition);
    void onConnectionFailed(String topic, String partition, int status, String error);
    void onConnectionClosed(String topic, String partition, Optional<Cursor> lastCursor);

    final class JEvent{

        private final String eventType;
        private final String orderingKey;
        private final Map<String, Object> metaData;
        private final Map<String, Object> body;

        public JEvent(String eventType, String orderingKey, Map<String, Object> metaData, Map<String, Object> body) {
            checkNotNull(eventType, "eventType must not be null");
            checkNotNull(orderingKey, "orderingKey must not be null");
            checkNotNull(metaData, "metaData must not be null");
            checkNotNull(body, "body must not be null");

            this.eventType = eventType;
            this.orderingKey = orderingKey;
            this.metaData = metaData;
            this.body = body;
        }

        public JEvent(final Event event) {
            checkNotNull(event, "event must not be null");

            eventType = event.eventType();
            orderingKey = event.orderingKey();
            metaData = ImmutableMap.copyOf(JavaConversions.mapAsJavaMap(event.metadata()));

            final scala.collection.immutable.Map<String, Object> bodyMap =
                                                        (scala.collection.immutable.Map<String, Object>) event.body();
            body = ImmutableMap.copyOf(JavaConversions.mapAsJavaMap(bodyMap));
        }

        public String getEventType() {
            return eventType;
        }

        public String getOrderingKey() {
            return orderingKey;
        }

        public Map<String, Object> getMetaData() {
            return mapDeeply(metaData);
        }


        private Map<String, Object> mapDeeply(final Map<String, Object> m) {
            final HashMap<String, Object> newMap = Maps.newHashMap();

            for(Map.Entry<String, Object> entry : m.entrySet()) {
                final String key = entry.getKey();
                final Object value = entry.getValue();

                if(value instanceof scala.collection.Map) {
                    final scala.collection.Map scalaMap = (scala.collection.Map) value;
                    newMap.put(key, mapDeeply((JavaConversions.mapAsJavaMap(scalaMap))));
                }
                else {
                    newMap.put(key, value);
                }
            }

            return newMap;
        }

        public Map<String, Object> getBody() {
            return mapDeeply(body);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("eventType", eventType)
                    .add("orderingKey", orderingKey)
                    .add("metaData", metaData)
                    .add("body", body)
                    .toString();
        }
    }
}
