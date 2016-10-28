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
    		 @JsonProperty("data")  T data, 
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
    @JsonProperty("data_op") 
    @Override
    public DataOperation getDataOperation() {
        return dataOperation;
    }

	public EventMetadata getMetadata() {
		return metadata;
	}
	
	
 

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        result = prime * result + ((dataOperation == null) ? 0 : dataOperation.hashCode());
        result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
        result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
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
        DataChangeEvent other = (DataChangeEvent) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        if (dataOperation != other.dataOperation)
            return false;
        if (dataType == null) {
            if (other.dataType != null)
                return false;
        } else if (!dataType.equals(other.dataType))
            return false;
        if (metadata == null) {
            if (other.metadata != null)
                return false;
        } else if (!metadata.equals(other.metadata))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DataChangeEvent [data=" + data + ", dataType=" + dataType + ", dataOperation=" + dataOperation + ", metadata=" + metadata + "]";
    }

	
}
