package org.zalando.nakadi.client.java.enumerator;


import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Indicator of the step in the pulbishing process this Event reached.
 * In Items that FAILED means the step of the failure.
 * - NONE indicates that nothing was yet attempted for the publishing of this Event. Should be present
 * only in the case of aborting the publishing during the validation of another (previous) Event. <br>
 * - VALIDATING, ENRICHING, PARTITIONING and PUBLISHING indicate all the corresponding steps of the
 * publishing process. <br> <br>
 * Values = NONE("none"), VALIDATING("validating"), ENRICHING("enriching"), PARTITIONING("partitioning"), PUBLISHING("publishing")
 */
public enum BatchItemStep {
	NONE("none"), //
	VALIDATING("validating"), //
	ENRICHING("enriching"), //
	PARTITIONING("partitioning"), //
	PUBLISHING("publishing");
	private final String step;

	private BatchItemStep(String step) {
		this.step = step;
	}
	@JsonValue
	public String getStep() {
		return step;
	}
	
	public static Optional<BatchItemStep> withName(String code) {
		for (BatchItemStep e : BatchItemStep.values()) {
			if (e != null && e.name().equals(code))
				return Optional.of(e);
		}
		return Optional.empty();
	}

}
