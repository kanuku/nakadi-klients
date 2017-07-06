package org.zalando.nakadi.client.java.test.event.generator;

import java.util.List;

import org.zalando.nakadi.client.java.enumerator.*;
import org.zalando.nakadi.client.java.model.*;

/**
 * Abstracts the necessary values that can be generated for tests.
 */
public interface EventGenerator {

    String getEventTypeId();

    String getNewId();

    Event getNewEvent();

    String getEventTypeName();

    String getSchemaDefinition();

    EventType getEventType();

    String getOwner();

    EventTypeCategory getCategory();

    List<EventEnrichmentStrategy> getEnrichmentStrategies();

    PartitionStrategy getPartitionStrategy();

    EventTypeSchema getSchemaType();

    List<String> getDataKeyFields();

    List<String> getPartitionKeyFields();

    EventTypeStatistics getStatistics();
    
    CompatibilityMode getCompatibilityMode();

}
