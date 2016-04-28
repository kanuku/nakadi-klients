package org.zalando.nakadi.client.java.enumerator;


import com.fasterxml.jackson.annotation.JsonValue;

import scala.Option;


/**
 * Indicator of the step in the pulbishing process this Event reached.
 * In Items that FAILED means the step of the failure.
 * - NONE indicates that nothing was yet attempted for the publishing of this Event. Should be present
 * only in the case of aborting the publishing during the validation of another (previous) Event. <br>
 * - VALIDATING, ENRICHING, PARTITIONING and PUBLISHING indicate all the corresponding steps of the
 * publishing process. <br> <br>
 * Values = NONE("NONE"), VALIDATING("VALIDATING"), ENRICHING("ENRICHING"), PARTITIONING("PARTITIONING"), PUBLISHING("PUBLISHING")
 */
public enum BatchItemStep {
	NONE("NONE"), //
	VALIDATING("VALIDATING"), //
	ENRICHING("ENRICHING"), //
	PARTITIONING("PARTITIONING"), //
	PUBLISHING("PUBLISHING");
	private final String step;

	private BatchItemStep(String step) {
		this.step = step;
	}
	@JsonValue
	public String getStep() {
		return step;
	}
	
	public static Option<BatchItemStep> withName(String code) {
		for (BatchItemStep e : BatchItemStep.values()) {
			if (e.step.equals(code))
				return Option.apply(e);
		}
		return Option.empty();
	}

}
