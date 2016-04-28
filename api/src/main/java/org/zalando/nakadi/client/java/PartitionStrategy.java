package org.zalando.nakadi.client.java;

import org.zalando.nakadi.client.model.PartitionStrategy;

import com.fasterxml.jackson.annotation.JsonValue;

import scala.Option;

/**
 * Defines a rule for the resolution of incoming Events into partitions. Rules
 * might require additional parameters; see the `doc` field of the existing
 * rules for details. See `GET /registry/partition-strategies` for a list of
 * available rules.
 */

public enum PartitionStrategy {
	HASH("hash"), //
	USER_DEFINED("user_defined"), //
	RANDOM("random");

	private final String strategy;

	private PartitionStrategy(String strategy) {
		this.strategy = strategy;
	}
	@JsonValue
	public String getStrategy() {
		return strategy;
	}

	public static Option<PartitionStrategy> withName(String code){
        for(PartitionStrategy e : PartitionStrategy.values()){
        	if (e.strategy.equals(code))
        	return Option.apply(e);
        }
        return Option.empty();
    }
}
