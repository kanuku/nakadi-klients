package org.zalando.nakadi.client.java.model;

import org.zalando.nakadi.client.java.enumerator.DataOperation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DataChangeEvent<T> implements DataChangeEventQualifier, Event {
    private final T data;
    private final String dataType;
    private final DataOperation dataOperation;
    private final EventMetadata metadata;

    @JsonCreator
    public DataChangeEvent(
    		 @JsonProperty  T data, 
    		 @JsonProperty("data_type") String dataType,
    		 @JsonProperty("data_op") DataOperation dataOperation,
    		 @JsonProperty("metadata")  EventMetadata metadata
    		 ) {
        this.data = data;
        this.dataType = dataType;
        this.dataOperation = dataOperation;
        this.metadata=metadata;
    }

    public T getData() {
        return data;
    }

    @Override
    public String getDataType() {
        return dataType;
    }

    @Override
    public DataOperation getDataOperation() {
        return dataOperation;
    }

	public EventMetadata getMetadata() {
		return metadata;
	}

}
