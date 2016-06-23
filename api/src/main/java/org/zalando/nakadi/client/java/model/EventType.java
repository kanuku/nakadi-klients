package org.zalando.nakadi.client.java.model;

import java.util.Collections;
import java.util.List;

import org.zalando.nakadi.client.java.enumerator.EventEnrichmentStrategy;
import org.zalando.nakadi.client.java.enumerator.EventTypeCategory;
import org.zalando.nakadi.client.java.enumerator.PartitionStrategy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An event type defines the schema and its runtime properties.
 * 
 *
 */
public class EventType {
    private final String name;
    private final String owningApplication;
    private final EventTypeCategory category;
    private final List<EventEnrichmentStrategy> enrichmentStrategies;
    private final PartitionStrategy partitionStrategy;
    private final EventTypeSchema schema;
    private final List<String> dataKeyFields;
    private final List<String> partitionKeyFields;
    private final EventTypeStatistics statistics;

    /**
     * An event type defines the schema and its runtime properties.
     * 
     * @param name
     * @param owningApplication
     *            Indicator of the Application owning this `EventType`.
     * @param category
     *            Defines the category of this EventType.
     * @param enrichmentStrategies
     *            Determines the enrichment to be performed on an Event upon reception.
     * @param partitionStrategy
     *            Determines how the assignment of the event to a Partition should be handled.
     * @param schema
     *            The schema for this EventType. This is expected to be a json schema in yaml format (other formats
     *            might be added in the future).
     * @param dataKeyFields
     *            Indicators of the path of the properties that constitute the primary key (identifier) of the data
     *            within this Event.
     * @param partitioningKeyFields
     *            Indicator of the field used for guaranteeing the ordering of Events of this type (used by the
     *            PartitionResolutionStrategy).
     * @param statistics
     *            Statistics of this EventType used for optimization purposes. Internal use of these values might change
     *            over time. (TBD: measured statistics).
     *
     */
    @JsonCreator
    public EventType(@JsonProperty("name") String name, @JsonProperty("owning_application") String owningApplication, @JsonProperty("category") EventTypeCategory category,
            @JsonProperty("enrichment_strategies") List<EventEnrichmentStrategy> enrichmentStrategies,
            @JsonProperty("partition_strategy") PartitionStrategy partitionStrategy, @JsonProperty("schema") EventTypeSchema schema, @JsonProperty("data_key_fields") List<String> dataKeyFields,
            @JsonProperty("partition_key_fields") List<String> partitionKeyFields, @JsonProperty("default_statistics") EventTypeStatistics statistics) {
        this.name = name;
        this.owningApplication = owningApplication;
        this.category = category;
        this.enrichmentStrategies = enrichmentStrategies;
        this.partitionStrategy = partitionStrategy;
        this.schema = schema;
        this.dataKeyFields = dataKeyFields;
        this.partitionKeyFields = partitionKeyFields;
        this.statistics = statistics;
    }

    private <T> List<T> unmodifiableList(List<T> in) {

        if (in == null || in.isEmpty()) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(in);
        }
    }

    public String getName() {
        return name;
    }

    public String getOwningApplication() {
        return owningApplication;
    }

    public EventTypeCategory getCategory() {
        return category;
    }


    public List<EventEnrichmentStrategy> getEnrichmentStrategies() {
        return unmodifiableList(enrichmentStrategies);
    }

    public PartitionStrategy getPartitionStrategy() {
        return partitionStrategy;
    }

    public EventTypeSchema getSchema() {
        return schema;
    }

    public List<String> getDataKeyFields() {
        return unmodifiableList(dataKeyFields);
    }

    public List<String> getPartitionKeyFields() {
        return unmodifiableList(partitionKeyFields);
    }

    public EventTypeStatistics getStatistics() {
        return statistics;
    }

    @Override
    public String toString() {
        return "EventType [name=" + name + ", owningApplication=" + owningApplication + ", category=" + category + ", enrichmentStrategies=" + enrichmentStrategies + ", partitionStrategy="
                + partitionStrategy + ", schema=" + schema + ", dataKeyFields=" + dataKeyFields + ", partitionKeyFields=" + partitionKeyFields + ", statistics=" + statistics + "]";
    }


}
