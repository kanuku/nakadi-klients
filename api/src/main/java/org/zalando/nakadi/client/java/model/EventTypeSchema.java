package org.zalando.nakadi.client.java.model;

import org.zalando.nakadi.client.java.enumerator.SchemaType;

import com.fasterxml.jackson.annotation.*;

/**
 * The schema for an EventType, expected to be a json schema in yaml
 * format (other formats might be added in the future).
 * @param type The type of schema definition (avro, json-schema, etc).
 * @param schema The schema as string in the syntax defined in the field type.
 * Failure to respect the syntax will fail any operation on an EventType.
 */

public class EventTypeSchema {
    private final SchemaType type;
    private final String schema;
    @JsonCreator
    public EventTypeSchema(
    		@JsonProperty("type")
    		SchemaType type, 
    		@JsonProperty("schema")
    		String schema) {
        this.type = type;
        this.schema = schema;
    }

    public SchemaType getType() {
        return type;
    }

    public String getSchema() {
        return schema;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((schema == null) ? 0 : schema.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EventTypeSchema other = (EventTypeSchema) obj;
        if (schema == null) {
            if (other.schema != null)
                return false;
        } else if (!schema.equals(other.schema))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "EventTypeSchema [type=" + type + ", schema=" + schema + "]";
    }
    
    

}
