package org.zalando.nakadi.client.java;

import org.zalando.nakadi.client.model.BatchItemPublishingStatus;

import scala.Option;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Indicator of the submission of the Event within a Batch. <br>
 * - SUBMITTED indicates successful submission, including commit on he underlying broker.<br>
 * - FAILED indicates the message submission was not possible and can be resubmitted if so desired.<br>
 * - ABORTED indicates that the submission of this item was not attempted any further due to a failure on another item in the batch.<br>
 * <br>
 */
public enum BatchItemPublishingStatus {
	SUBMITTED("SUBMITTED"), //
	FAILED("FAILED"), //
	ABORTED("ABORTED");
	private final String status;

	private BatchItemPublishingStatus(String status) {
		this.status = status;
	}

	@JsonValue
	public String getStatus() {
		return status;
	};

	public static Option<BatchItemPublishingStatus> withName(String code) {
		for (BatchItemPublishingStatus e : BatchItemPublishingStatus.values()) {
			if (e.status.equals(code))
				return Option.apply(e);
		}
		return Option.empty();
	}
}
