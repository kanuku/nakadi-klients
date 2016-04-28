package org.zalando.nakadi.client.java;

import org.zalando.nakadi.client.model.EventEnrichmentStrategy;

import com.fasterxml.jackson.annotation.JsonValue;

import scala.Option;

/**
 * Defines a rule for transformation of an incoming Event. No existing fields
 * might be modified. In practice this is used to set automatically values in
 * the Event upon reception (i.e. set a reception timestamp on the Event). Rules
 * might require additional parameters; see the `doc` field of the existing
 * rules for details. See GET /registry/enrichment-strategies for a list of
 * available rules.
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
	public static Option<EventEnrichmentStrategy> withName(String code) {
		for (EventEnrichmentStrategy e : EventEnrichmentStrategy.values()) {
			if (e.metadata.equals(code))
				return Option.apply(e);
		}
		return Option.empty();
	}
}
