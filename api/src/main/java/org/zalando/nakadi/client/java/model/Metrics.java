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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((gauges == null) ? 0 : gauges.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Metrics other = (Metrics) obj;
        if (gauges == null) {
            if (other.gauges != null)
                return false;
        } else if (!gauges.equals(other.gauges))
            return false;
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Metrics [version=" + version + ", gauges=" + gauges + "]";
    }

}
