package org.zalando.nakadi.client.java.enumerator;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Indicator of the submission of the Event within a Batch. <br>
 * - SUBMITTED indicates successful submission, including commit on he
 * underlying broker.<br>
 * - FAILED indicates the message submission was not possible and can be
 * resubmitted if so desired.<br>
 * - ABORTED indicates that the submission of this item was not attempted any
 * further due to a failure on another item in the batch.<br>
 * <br>
 */
public enum BatchItemPublishingStatus {
    SUBMITTED("submitted"), //
    FAILED("failed"), //
    ABORTED("aborted");
    private final String value;

    private BatchItemPublishingStatus(String value) {
        this.value = value;
    }

    /**
     * Use the method {@link #getValue()}
     * 
     * @return
     */
    @Deprecated()
    @JsonIgnore
    public String getStatus() {
        return value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static Optional<BatchItemPublishingStatus> withName(String name) {
        return EnumUtil.withName(name, BatchItemPublishingStatus.class);
    }
}
