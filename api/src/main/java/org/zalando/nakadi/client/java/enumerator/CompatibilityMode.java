package org.zalando.nakadi.client.java.enumerator;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Compatibility mode provides a mean for event owners to evolve their schema,
 * given changes respect the semantics defined by this field. <br>
 * It's designed to be flexible enough so that producers can evolve their
 * schemas while not inadvertently breaking existent consumers.<br>
 * Once defined, the compatibility mode is fixed, since otherwise it would break
 * a predefined contract, declared by the producer. <br>
 * <p>
 * FIXED - Schema changes are not allowed. This is used only by legacy event
 * types. This option is not available for new event types. Schema validation is
 * lenient and allow fields that are not defined. Also, it's possible to use the
 * full json schema specification for defining schemas.
 * <p>
 * NONE - Any schema modification is accepted, even if it might break existing
 * producers or consumers. When validating events, no additional properties are
 * accepted unless explicitly stated in the schema.
 * <p>
 * COMPATIBLE (default) - Consumers can reliably parse events produced under
 * different versions. Every event published since the first version is still
 * valid based on the newest schema. When in compatible mode, it's allowed to
 * add new optional properties and definitions to an existing schema, but no
 * other changes are allowed. Under this mode, the following json-schema
 * attributes are not supported: `not`, `patternProperties`,
 * `additionalProperties` and `additionalItems`. When validating events,
 * additional properties is `false`.
 * 
 */
public enum CompatibilityMode {
    FIXED("fixed"), //
    NONE("none"), //
    COMPATIBLE("compatible");

    private final String mode;

    private CompatibilityMode(String mode) {
        this.mode = mode;
    }

    @JsonValue
    public String getCompatibilityMode() {
        return mode;
    }

    public static Optional<CompatibilityMode> withName(String code) {
        for (CompatibilityMode e : CompatibilityMode.values()) {
            if (e != null && e.name().equals(code))
                return Optional.of(e);
        }
        return Optional.empty();
    }
}
