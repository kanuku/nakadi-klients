package org.zalando.nakadi.client.examples.java;

import org.zalando.nakadi.client.java.model.Event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MeetingsEvent  implements Event {
    private final String date;
    private final String topic;

    @JsonCreator
    public MeetingsEvent(@JsonProperty("date") String date, @JsonProperty("topic") String topic) {
        this.date = date;
        this.topic = topic;
    }

    public String getDate() {
        return date;
    }

    public String getTopic() {
        return topic;
    }

}