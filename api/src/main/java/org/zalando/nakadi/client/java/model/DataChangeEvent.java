package org.zalando.nakadi.client.java.model;

import org.zalando.nakadi.client.java.enumerator.DataOperation;

public class DataChangeEvent<T> implements DataChangeEventQualifier, Event {
    private final T data;
    private final String dataType;
    private final DataOperation dataOperation;

    public DataChangeEvent(T data, String dataType, DataOperation dataOperation) {
        this.data = data;
        this.dataType = dataType;
        this.dataOperation = dataOperation;
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

}
