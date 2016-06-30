package org.zalando.nakadi.client.java.enumerator;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SchemaType {
	JSON("json_schema");

	private final String schema;

	private SchemaType(String schema) {
		this.schema = schema;
	}
	@JsonValue
	public String getSchema() {
		return schema;
	}

	public static Optional<SchemaType> withName(String code) {
		for (SchemaType e : SchemaType.values()) {
			if (e != null && e.name().equals(code))
				return Optional.of(e);
		}
		return Optional.empty();
	}

}
