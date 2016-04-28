package org.zalando.nakadi.client.java;

import org.zalando.nakadi.client.model.EventValidationStrategy;

import com.fasterxml.jackson.annotation.JsonValue;

import scala.Option;

/**
 * Defines a rule for validation of an incoming Event. Rules might require
 * additional parameters; see the `doc` field of the existing rules for details.
 * See GET /registry/validation-strategies for a list of available rules.
 */
public enum EventValidationStrategy {
	NONE("None");
	private final String strategy;

	private EventValidationStrategy(String strategy) {
		this.strategy = strategy;
	}
	@JsonValue
	public String getStrategy() {
		return strategy;
	}

	public static Option<EventValidationStrategy> withName(String code) {
		for (EventValidationStrategy e : EventValidationStrategy.values()) {
			if (e.strategy.equals(code))
				return Option.apply(e);
		}
		return Option.empty();
	}
}
