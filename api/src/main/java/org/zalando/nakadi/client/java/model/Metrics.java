package org.zalando.nakadi.client.java.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Metrics {
    private final String version;
    private final Map<String, Object> gauges;

    @JsonCreator
    public Metrics(@JsonProperty("version") String version, @JsonProperty("gauges") Map<String, Object> gauges) {
        this.version = version;
        this.gauges = gauges;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, Object> getGauges() {
        return gauges;
    }

    @Override
    public String toString() {
        return "Metrics [version=" + version + ", gauges=" + gauges + "]";
    }

}
