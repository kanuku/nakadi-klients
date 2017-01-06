package org.zalando.nakadi.client.java.test.event.generator;

import java.util.List;

import org.zalando.nakadi.client.java.enumerator.CompatibilityMode;
import org.zalando.nakadi.client.java.enumerator.EventEnrichmentStrategy;
import org.zalando.nakadi.client.java.enumerator.EventTypeCategory;
import org.zalando.nakadi.client.java.enumerator.PartitionStrategy;
import org.zalando.nakadi.client.java.model.Event;
import org.zalando.nakadi.client.java.model.EventType;
import org.zalando.nakadi.client.java.model.EventTypeSchema;
import org.zalando.nakadi.client.java.model.EventTypeStatistics;

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
