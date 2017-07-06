package org.zalando.nakadi.client.java.enumerator;

import java.util.Optional;

import com.fasterxml.jackson.annotation.*;

/**
 * Identifier for the type of operation to executed on the entity. <br>
 * C: Creation <br>
 * U: Update <br>
 * D: Deletion <br>
 * S: Snapshot <br>
 * <br>
 */
public enum DataOperation {
    CREATE("C"), UPDATE("U"), DELETE("D"), SNAPSHOT("S");
    private final String value;

    private DataOperation(String value) {
        this.value = value;
    }

    /**
     * Use the method {@link #getValue()}
     * 
     * @return
     */
    @Deprecated()
    @JsonIgnore
    public String getOperation() {
        return value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static Optional<DataOperation> withName(String name) {
        return EnumUtil.withName(name, DataOperation.class);
    }
}
