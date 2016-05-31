package org.zalando.nakadi.client.java.enumerator;

import scala.Option;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SchemaType {
	JSON("JSON_SCHEMA");

	private final String schema;

	private SchemaType(String schema) {
		this.schema = schema;
	}
	@JsonValue
	public String getSchema() {
		return schema;
	}

	public static Option<SchemaType> withName(String code) {
		for (SchemaType e : SchemaType.values()) {
			if (e.schema.equals(code))
				return Option.apply(e);
		}
		return Option.empty();
	}

}
