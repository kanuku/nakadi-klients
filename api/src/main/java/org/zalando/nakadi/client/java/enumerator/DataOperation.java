package org.zalando.nakadi.client.java.enumerator;


import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;


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
	private final String operation;

	private DataOperation(String operation) {
		this.operation = operation;
	}
	@JsonValue
	public String getOperation() {
		return operation;
	}

	public static Optional<DataOperation> withName(String code){
        for(DataOperation e : DataOperation.values()){
        	if (e != null && e.name().equals(code))
        	return Optional.of(e);
        }
        return Optional.empty();
    }
}
