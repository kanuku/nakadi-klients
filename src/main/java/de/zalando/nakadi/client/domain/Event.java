package de.zalando.nakadi.client.domain;


import com.google.common.base.MoreObjects;

import java.util.Map;

public class Event {

    private String eventType;
    private String orderingKey;
    private Map<String, Object> metadata;
    private Object body;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getOrderingKey() {
        return orderingKey;
    }

    public void setOrderingKey(String orderingKey) {
        this.orderingKey = orderingKey;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("eventType", eventType)
                .add("orderingKey", orderingKey)
                .add("metadata", metadata)
                .add("body", body)
                .toString();
    }
}
