package org.zalando.nakadi.client.java.model;

import com.fasterxml.jackson.annotation.*;

public class BusinessEventImpl   implements BusinessEvent {
    private EventMetadata metadata;

    @JsonCreator
    public BusinessEventImpl(  @JsonProperty("metadata")EventMetadata metadata) {
        this.metadata = metadata;
    }

   
    public EventMetadata metadata() {
        return metadata;
    }
    public EventMetadata getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "BusinessEventImpl [eventMetdata=" + metadata + "]";
    }
    
}