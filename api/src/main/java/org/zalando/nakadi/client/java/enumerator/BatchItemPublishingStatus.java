package org.zalando.nakadi.client.java.enumerator;


import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Indicator of the submission of the Event within a Batch. <br>
 * - SUBMITTED indicates successful submission, including commit on he underlying broker.<br>
 * - FAILED indicates the message submission was not possible and can be resubmitted if so desired.<br>
 * - ABORTED indicates that the submission of this item was not attempted any further due to a failure on another item in the batch.<br>
 * <br>
 */
public enum BatchItemPublishingStatus {
	SUBMITTED("submitted"), //
	FAILED("failed"), //
	ABORTED("aborted");
	private final String status;

	private BatchItemPublishingStatus(String status) {
		this.status = status;
	}

	@JsonValue
	public String getStatus() {
		return status;
	};

	public static Optional<BatchItemPublishingStatus> withName(String name) {
		for (BatchItemPublishingStatus e : BatchItemPublishingStatus.values()) {
			if (e != null && e.name().equals(name))
				return Optional.of(e);
		}
		return Optional.empty();
	}
}
