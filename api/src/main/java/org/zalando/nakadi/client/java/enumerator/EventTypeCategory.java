package org.zalando.nakadi.client.java.enumerator;

import java.util.Optional;

import com.fasterxml.jackson.annotation.*;

/**
 * Defines the category of an EventType. <br>
 */
public enum EventTypeCategory {
    UNDEFINED("undefined"), //
    DATA("data"), //
    BUSINESS("business");

    private final String value;

    private EventTypeCategory(String value) {
        this.value = value;
    }

    /**
     * Use the method {@link #getValue()}
     * 
     * @return
     */
    @Deprecated()
    @JsonIgnore
    public String getCategory() {
        return value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static Optional<EventTypeCategory> withName(String name) {
        return EnumUtil.withName(name, EventTypeCategory.class);
    }
}
