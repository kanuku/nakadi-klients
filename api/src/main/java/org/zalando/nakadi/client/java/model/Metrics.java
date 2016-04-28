package org.zalando.nakadi.client.java.model;

import java.util.Map;

public class Metrics {
    private final Map<String,Object> metrics;

    public Metrics(Map<String, Object> metrics) {
        this.metrics = metrics;
    }

    public Map<String, Object> getMetrics() {
        return metrics;
    }
    

}
