package org.zalando.nakadi.client.java.enumerator;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Indicator of the step in the pulbishing process this Event reached. In Items
 * that FAILED means the step of the failure. - NONE indicates that nothing was
 * yet attempted for the publishing of this Event. Should be present only in the
 * case of aborting the publishing during the validation of another (previous)
 * Event. <br>
 * - VALIDATING, ENRICHING, PARTITIONING and PUBLISHING indicate all the
 * corresponding steps of the publishing process. <br>
 * <br>
 * Values = NONE("none"), VALIDATING("validating"), ENRICHING("enriching"),
 * PARTITIONING("partitioning"), PUBLISHING("publishing")
 */
public enum BatchItemStep {
    NONE("none"), //
    VALIDATING("validating"), //
    ENRICHING("enriching"), //
    PARTITIONING("partitioning"), //
    PUBLISHING("publishing");
    private final String value;

    private BatchItemStep(String value) {
        this.value = value;
    }

    /**
     * Use the method {@link #getValue()}
     * 
     * @return
     */
    @Deprecated()
    @JsonIgnore
    public String getStep() {
        return value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static Optional<BatchItemStep> withName(String name) {
        return EnumUtil.withName(name, BatchItemStep.class);
    }

    public static Optional<BatchItemStep> withValue(String value) {
        return EnumUtil.withName(value, BatchItemStep.class);
    }

}
