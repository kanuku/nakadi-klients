package org.zalando.nakadi.client.java;

import org.zalando.nakadi.client.model.EventTypeCategory;

import com.fasterxml.jackson.annotation.JsonValue;

import scala.Option;

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

	public static Option<EventTypeCategory> withName(String code){
        for(EventTypeCategory e : EventTypeCategory.values()){
        	if (e.category.equals(code))
        	return Option.apply(e);
        }
        return Option.empty();
    }
}
