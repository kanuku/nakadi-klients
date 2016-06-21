package org.zalando.nakadi.client.java.test.factory;

import org.zalando.nakadi.client.java.model.Event;
import org.zalando.nakadi.client.java.test.factory.events.MySimpleEvent;

public class MySimpleEventGenerator extends EventGeneratorBuilder {

    protected Event getNewEvent() {
        return new MySimpleEvent(getNewId());
    }

    @Override
    protected String getEventTypeName() {
        return getEventTypeId() + getNewId();
    }

    @Override
    protected String getSchemaDefinition() {
        return "{ 'properties': { 'order_number': { 'type': 'string' } } }".replaceAll("'", "\"");
    }

}
