package org.zalando.nakadi.client.java.enumerator;

import java.util.Optional;

import com.fasterxml.jackson.annotation.*;

/**
 * Determines the enrichment to be performed on an Event upon reception(server
 * side). Enrichment is performed once upon reception (and after validation) of
 * an Event and is only possible on fields that are not defined on the incoming
 * Event.
 */

public enum EventEnrichmentStrategy {
    METADATA("metadata_enrichment");

    private final String value;

    private EventEnrichmentStrategy(String value) {
        this.value = value;
    }

    /**
     * Use the method {@link #getValue()}
     * 
     * @return
     */
    @Deprecated()
    @JsonIgnore
    public String getMetadata() {
        return value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static Optional<EventEnrichmentStrategy> withName(String name) {
        return EnumUtil.withName(name, EventEnrichmentStrategy.class);
    }
}
