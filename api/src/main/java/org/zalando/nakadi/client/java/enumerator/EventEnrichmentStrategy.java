package org.zalando.nakadi.client.java.enumerator;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Determines the enrichment to be performed on an Event upon reception(server side). Enrichment is performed once upon
 * reception (and after validation) of an Event and is only possible on fields that are not defined on the incoming
 * Event.
 */

public enum EventEnrichmentStrategy {
    METADATA("metadata_enrichment");

    private final String metadata;

    private EventEnrichmentStrategy(String metadata) {
        this.metadata = metadata;
    }

    @JsonValue
    public String getMetadata() {
        return metadata;
    }

    public static Optional<EventEnrichmentStrategy> withName(String code) {
        for (EventEnrichmentStrategy e : EventEnrichmentStrategy.values()) {
            if (e != null && e.name().equals(code))
                return Optional.of(e);
        }
        return Optional.empty();
    }
}
