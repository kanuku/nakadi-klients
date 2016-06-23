package org.zalando.nakadi.client.java.enumerator;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Defines the category of an EventType. <br>
 */
public enum EventTypeCategory {
    UNDEFINED("undefined"), //
    DATA("data"), //
    BUSINESS("business");

    private final String category;

    private EventTypeCategory(String category) {
        this.category = category;
    }

    @JsonValue
    public String getCategory() {
        return category;
    }

    public static Optional<EventTypeCategory> withName(String code) {
        for (EventTypeCategory e : EventTypeCategory.values()) {
            if (e != null && e.category.equals(code))
                return Optional.of(e);
        }
        return Optional.empty();
    }
}
