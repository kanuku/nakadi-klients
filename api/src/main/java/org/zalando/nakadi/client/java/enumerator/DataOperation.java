package org.zalando.nakadi.client.java.enumerator;


import com.fasterxml.jackson.annotation.JsonValue;

import scala.Option;


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

	public static Option<DataOperation> withName(String code){
        for(DataOperation e : DataOperation.values()){
        	if (e.operation.equals(code))
        	return Option.apply(e);
        }
        return Option.empty();
    }
}
