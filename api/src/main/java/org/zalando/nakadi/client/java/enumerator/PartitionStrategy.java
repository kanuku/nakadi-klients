package org.zalando.nakadi.client.java.enumerator;

import java.util.Optional;

import com.fasterxml.jackson.annotation.*;

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

    private final String value;

    private PartitionStrategy(String value) {
        this.value = value;
    }

    /**
     * Use the method {@link #getValue()}
     * 
     * @return
     */
    @Deprecated()
    @JsonIgnore
    public String getStrategy() {
        return value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static Optional<PartitionStrategy> withName(String name) {
        return EnumUtil.withName(name, PartitionStrategy.class);
    }
}
