package org.zalando.nakadi.client.java.model;

import java.util.Collections;
import java.util.List;

import org.zalando.nakadi.client.java.enumerator.CompatibilityMode;
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
    private final CompatibilityMode compatibilityMode;

    /**
     * An event type defines the schema and its runtime properties.
     * 
     * @param name
     * @param owningApplication
     *            Indicator of the Application owning this `EventType`.
     * @param category
     *            Defines the category of this EventType.
     * @param enrichmentStrategies
     *            Determines the enrichment to be performed on an Event upon
     *            reception.
     * @param partitionStrategy
     *            Determines how the assignment of the event to a Partition
     *            should be handled.
     * @param schema
     *            The schema for this EventType. This is expected to be a json
     *            schema in yaml format (other formats might be added in the
     *            future).
     * @param dataKeyFields
     *            Indicators of the path of the properties that constitute the
     *            primary key (identifier) of the data within this Event.
     * @param partitioningKeyFields
     *            Indicator of the field used for guaranteeing the ordering of
     *            Events of this type (used by the PartitionResolutionStrategy).
     * @param statistics
     *            Statistics of this EventType used for optimization purposes.
     *            Internal use of these values might change over time. (TBD:
     *            measured statistics).
     *
     */
    public EventType(String name, String owningApplication, EventTypeCategory category, List<EventEnrichmentStrategy> enrichmentStrategies,
            PartitionStrategy partitionStrategy, EventTypeSchema schema, List<String> dataKeyFields, List<String> partitionKeyFields,
            EventTypeStatistics statistics) {
        this(name, owningApplication, category, enrichmentStrategies, partitionStrategy, schema, dataKeyFields, partitionKeyFields,
                statistics, null);
    }

    /**
     * An event type defines the schema and its runtime properties.
     * 
     * @param name
     * @param owningApplication
     *            Indicator of the Application owning this `EventType`.
     * @param category
     *            Defines the category of this EventType.
     * @param enrichmentStrategies
     *            Determines the enrichment to be performed on an Event upon
     *            reception.
     * @param partitionStrategy
     *            Determines how the assignment of the event to a Partition
     *            should be handled.
     * @param schema
     *            The schema for this EventType. This is expected to be a json
     *            schema in yaml format (other formats might be added in the
     *            future).
     * @param dataKeyFields
     *            Indicators of the path of the properties that constitute the
     *            primary key (identifier) of the data within this Event.
     * @param partitioningKeyFields
     *            Indicator of the field used for guaranteeing the ordering of
     *            Events of this type (used by the PartitionResolutionStrategy).
     * @param statistics
     *            Statistics of this EventType used for optimization purposes.
     *            Internal use of these values might change over time. (TBD:
     *            measured statistics).
     * @param compatibilityMode
     *
     */
    @JsonCreator
    public EventType(@JsonProperty("name") String name, @JsonProperty("owning_application") String owningApplication,
            @JsonProperty("category") EventTypeCategory category,
            @JsonProperty("enrichment_strategies") List<EventEnrichmentStrategy> enrichmentStrategies,
            @JsonProperty("partition_strategy") PartitionStrategy partitionStrategy, @JsonProperty("schema") EventTypeSchema schema,
            @JsonProperty("data_key_fields") List<String> dataKeyFields,
            @JsonProperty("partition_key_fields") List<String> partitionKeyFields,
            @JsonProperty("default_statistic") EventTypeStatistics statistics,
            @JsonProperty("compatibility_mode") CompatibilityMode compatibilityMode) {
        this.name = name;
        this.owningApplication = owningApplication;
        this.category = category;
        this.enrichmentStrategies = enrichmentStrategies;
        this.partitionStrategy = partitionStrategy;
        this.schema = schema;
        this.dataKeyFields = dataKeyFields;
        this.partitionKeyFields = partitionKeyFields;
        this.statistics = statistics;
        this.compatibilityMode = compatibilityMode;
    }

    private <T> List<T> unmodifiableList(List<T> in) {

        if (in == null || in.isEmpty()) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(in);
        }
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("owning_application")
    public String getOwningApplication() {
        return owningApplication;
    }

    @JsonProperty("category")
    public EventTypeCategory getCategory() {
        return category;
    }

    @JsonProperty("enrichment_strategies")
    public List<EventEnrichmentStrategy> getEnrichmentStrategies() {
        return unmodifiableList(enrichmentStrategies);
    }

    @JsonProperty("partition_strategy")
    public PartitionStrategy getPartitionStrategy() {
        return partitionStrategy;
    }

    @JsonProperty("schema")
    public EventTypeSchema getSchema() {
        return schema;
    }

    @JsonProperty("data_key_fields")
    public List<String> getDataKeyFields() {
        return unmodifiableList(dataKeyFields);
    }

    @JsonProperty("partition_key_fields")
    public List<String> getPartitionKeyFields() {
        return unmodifiableList(partitionKeyFields);
    }

    @JsonProperty("default_statistic")
    public EventTypeStatistics getStatistics() {
        return statistics;
    }

    @JsonProperty("compatibility_mode")
    public CompatibilityMode getCompatibilityMode() {
        return compatibilityMode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((compatibilityMode == null) ? 0 : compatibilityMode.hashCode());
        result = prime * result + ((dataKeyFields == null) ? 0 : dataKeyFields.hashCode());
        result = prime * result + ((enrichmentStrategies == null) ? 0 : enrichmentStrategies.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((owningApplication == null) ? 0 : owningApplication.hashCode());
        result = prime * result + ((partitionKeyFields == null) ? 0 : partitionKeyFields.hashCode());
        result = prime * result + ((partitionStrategy == null) ? 0 : partitionStrategy.hashCode());
        result = prime * result + ((schema == null) ? 0 : schema.hashCode());
        result = prime * result + ((statistics == null) ? 0 : statistics.hashCode());
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
        EventType other = (EventType) obj;
        if (category != other.category)
            return false;
        if (compatibilityMode != other.compatibilityMode)
            return false;
        if (dataKeyFields == null) {
            if (other.dataKeyFields != null)
                return false;
        } else if (!dataKeyFields.equals(other.dataKeyFields))
            return false;
        if (enrichmentStrategies == null) {
            if (other.enrichmentStrategies != null)
                return false;
        } else if (!enrichmentStrategies.equals(other.enrichmentStrategies))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (owningApplication == null) {
            if (other.owningApplication != null)
                return false;
        } else if (!owningApplication.equals(other.owningApplication))
            return false;
        if (partitionKeyFields == null) {
            if (other.partitionKeyFields != null)
                return false;
        } else if (!partitionKeyFields.equals(other.partitionKeyFields))
            return false;
        if (partitionStrategy != other.partitionStrategy)
            return false;
        if (schema == null) {
            if (other.schema != null)
                return false;
        } else if (!schema.equals(other.schema))
            return false;
        if (statistics == null) {
            if (other.statistics != null)
                return false;
        } else if (!statistics.equals(other.statistics))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "EventType [name=" + name + ", owningApplication=" + owningApplication + ", category=" + category + ", enrichmentStrategies="
                + enrichmentStrategies + ", partitionStrategy=" + partitionStrategy + ", schema=" + schema + ", dataKeyFields="
                + dataKeyFields + ", partitionKeyFields=" + partitionKeyFields + ", statistics=" + statistics + ", compatibilityMode="
                + compatibilityMode + "]";
    }

}
