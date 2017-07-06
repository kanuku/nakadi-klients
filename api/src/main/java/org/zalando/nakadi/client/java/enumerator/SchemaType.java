package org.zalando.nakadi.client.java.enumerator;

import java.util.Optional;

import com.fasterxml.jackson.annotation.*;

public enum SchemaType {
    JSON("json_schema");

    private final String value;

    private SchemaType(String value) {
        this.value = value;
    }

    /**
     * Use the method {@link #getValue()}
     * 
     * @return
     */
    @Deprecated()
    @JsonIgnore
    public String getSchema() {
        return value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static Optional<SchemaType> withName(String name) {
        return EnumUtil.withName(name, SchemaType.class);
    }

}
