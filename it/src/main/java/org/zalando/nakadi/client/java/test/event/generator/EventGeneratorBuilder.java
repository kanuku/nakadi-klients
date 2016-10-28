package org.zalando.nakadi.client.java.test.event.generator;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.zalando.nakadi.client.java.enumerator.EventEnrichmentStrategy;
import org.zalando.nakadi.client.java.enumerator.EventTypeCategory;
import org.zalando.nakadi.client.java.enumerator.PartitionStrategy;
import org.zalando.nakadi.client.java.enumerator.SchemaType;
import org.zalando.nakadi.client.java.model.Event;
import org.zalando.nakadi.client.java.model.EventType;
import org.zalando.nakadi.client.java.model.EventTypeSchema;
import org.zalando.nakadi.client.java.model.EventTypeStatistics;

public abstract class EventGeneratorBuilder {
    private String eventTypeId;
    private Integer newId = new Integer(0);
    private Event newEvent;
    private String schemaDefinition;
    private EventType eventType = null;
    private String owner = "stups_Nakadi-klients(java-integration-test-suite)";
    private EventTypeCategory category = EventTypeCategory.UNDEFINED;
    private List<EventEnrichmentStrategy> enrichmentStrategies = Collections.emptyList();
    private PartitionStrategy partitionStrategy = PartitionStrategy.RANDOM;
    private EventTypeSchema schemaType;
    private List<String> dataKeyFields = Collections.emptyList();
    private List<String> partitionKeyFields = Collections.emptyList();
    private EventTypeStatistics statistics = null;

    public EventGeneratorBuilder withEventTypeId(String eventTypeId) {
        this.eventTypeId = eventTypeId;
        return this;
    }

    public EventGeneratorBuilder withNewId(Integer newId) {
        this.newId = newId;
        return this;
    }

    public EventGeneratorBuilder withNewEvent(Event newEvent) {
        this.newEvent = newEvent;
        return this;
    }

    public EventGeneratorBuilder withSchemaDefinition(String schemaDefinition) {
        this.schemaDefinition = schemaDefinition;
        return this;
    }

    public EventGeneratorBuilder withEventType(EventType eventType) {
        this.eventType = eventType;
        return this;
    }

    public EventGeneratorBuilder withOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public EventGeneratorBuilder withCategory(EventTypeCategory category) {
        this.category = category;
        return this;
    }

   

    public EventGeneratorBuilder withEnrichmentStrategies(List<EventEnrichmentStrategy> enrichmentStrategies) {
        this.enrichmentStrategies = enrichmentStrategies;
        return this;
    }

    public EventGeneratorBuilder withPartitionStrategy(PartitionStrategy partitionStrategy) {
        this.partitionStrategy = partitionStrategy;
        return this;
    }

    public EventGeneratorBuilder withSchemaType(EventTypeSchema schemaType) {
        this.schemaType = schemaType;
        return this;
    }

    public EventGeneratorBuilder withDataKeyFields(List<String> dataKeyFields) {
        this.dataKeyFields = dataKeyFields;
        return this;
    }

    public EventGeneratorBuilder withPartitionKeyFields(List<String> partitionKeyFields) {
        this.partitionKeyFields = partitionKeyFields;
        return this;
    }

    public EventGeneratorBuilder withStatistics(EventTypeStatistics statistics) {
        this.statistics = statistics;
        return this;
    }

    public EventGenerator build() {
        EventGeneratorBuilder gen = this;
        return new EventGenerator() { // Defaults

            @Override
            public String getEventTypeId() {
                return gen.getEventTypeId();
            }

            @Override
            public String getNewId() {
                return gen.getNewId().toString();
            }

            @Override
            public Event getNewEvent() {
                return gen.getNewEvent();
            }

            @Override
            public String getEventTypeName() {
                return gen.getEventTypeName();
            }

            @Override
            public String getSchemaDefinition() {
                return gen.getSchemaDefinition();
            }

            @Override
            public EventType getEventType() {
                return new EventType(gen.getEventTypeName(), //
                        gen.getOwner(), //
                        gen.getCategory(),//
                        gen.getEnrichmentStrategies(),//
                        gen.getPartitionStrategy(), //
                        gen.getSchemaType(), //
                        gen.getDataKeyFields(), //
                        gen.getPartitionKeyFields(), //
                        gen.getStatistics());
            }

            @Override
            public String getOwner() {
                return owner;
            }

            @Override
            public EventTypeCategory getCategory() {
                return category;
            }

            @Override
            public List<EventEnrichmentStrategy> getEnrichmentStrategies() {
                return enrichmentStrategies;
            }

            @Override
            public PartitionStrategy getPartitionStrategy() {
                return partitionStrategy;
            }

            @Override
            public EventTypeSchema getSchemaType() {
                return schemaType;
            }

            @Override
            public List<String> getDataKeyFields() {
                return dataKeyFields;
            }

            @Override
            public List<String> getPartitionKeyFields() {
                return partitionKeyFields;
            }

            @Override
            public EventTypeStatistics getStatistics() {
                return statistics;
            }

        };

    }

    /**
     * Defaults or Lazy values should be defined under this line
     */
    protected EventTypeSchema getSchemaType() {
        return new EventTypeSchema(SchemaType.JSON, getSchemaDefinition());
    }

    protected String getEventTypeName() {
        return getEventTypeId() + getNewId();
    }

    /**
     * End of Defaults
     */
    protected String getEventTypeId() {
        return eventTypeId;
    }

    protected String getNewId() {
        return newId.toString();
    }

    protected Event getNewEvent() {
        return newEvent;
    }

    protected String getSchemaDefinition() {
        return schemaDefinition;
    }

    protected EventType getEventType() {
        return eventType;
    }

    protected String getOwner() {
        return owner;
    }

    protected EventTypeCategory getCategory() {
        return category;
    }

     
    protected List<EventEnrichmentStrategy> getEnrichmentStrategies() {
        return enrichmentStrategies;
    }

    protected PartitionStrategy getPartitionStrategy() {
        return partitionStrategy;
    }

    protected List<String> getDataKeyFields() {
        return dataKeyFields;
    }

    protected List<String> getPartitionKeyFields() {
        return partitionKeyFields;
    }

    protected EventTypeStatistics getStatistics() {
        return statistics;
    }
    
    public String randomNumeric(int size) {
        return RandomStringUtils.randomNumeric(size);
    }
    public String randomAlphanumeric(int size) {
        return RandomStringUtils.randomAlphanumeric(size);
    }

}
