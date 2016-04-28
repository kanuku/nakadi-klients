package org.zalando.nakadi.client.java.model;

import org.zalando.nakadi.client.java.enumerator.DataOperation;

public interface DataChangeEventQualifier {
    String getDataType();
    DataOperation getDataOperation();
}
