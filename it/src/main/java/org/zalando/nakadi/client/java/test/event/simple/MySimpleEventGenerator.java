package org.zalando.nakadi.client.java.test.event.simple;

import org.apache.commons.lang.RandomStringUtils;
import org.zalando.nakadi.client.java.model.Event;
import org.zalando.nakadi.client.java.test.event.generator.EventGeneratorBuilder;

public class MySimpleEventGenerator extends EventGeneratorBuilder {
    private final String id = RandomStringUtils.randomAlphanumeric(12);

    protected Event getNewEvent() {
        return new MySimpleEvent(getNewId());
    }

    @Override
    protected String getEventTypeName() {
        return getEventTypeId() + id;
    }

    @Override
    protected String getSchemaDefinition() {
        return "{ 'properties': { 'order_number': { 'type': 'string' } } }".replaceAll("'", "\"");
        
    }

}
