package org.zalando.nakadi.client.java.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Metrics {
    private final Map<String, Object> metrics;

    @JsonCreator
    public Metrics(@JsonProperty Map<String, Object> metrics) {
        this.metrics = metrics;
    }

    public Map<String, Object> getMetrics() {
        return metrics;
    }

    @Override
    public String toString() {
        return "Metrics [metrics=" + metrics + "]";
    }

}
