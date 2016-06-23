package org.zalando.nakadi.client.java.model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.zalando.nakadi.client.java.enumerator.EventEnrichmentStrategy;
import org.zalando.nakadi.client.java.enumerator.EventTypeCategory;
import org.zalando.nakadi.client.java.enumerator.EventValidationStrategy;
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
    private final List<EventValidationStrategy> validationStrategies;
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
     *            Name of this EventType. Encodes the owner/responsible for this EventType. The name for the EventType
     *            SHOULD follow the pattern, but is not enforced 'stups_owning_application.event-type', for example
     *            'gizig.price-change'. The components of the name are: * Organization: the organizational unit where
     *            the team is located; can be omitted. * Team name: name of the team responsible for owning application;
     *            can be omitted. * Owning application: SHOULD match the field owning_application; indicates * EventType
     *            name: name of this EventType; SHOULD end in ChangeEvent for DataChangeEvents; MUST be in the past
     *            tense for BusinessEvents. (TBD: how to deal with organizational changes? Should be reflected on the
     *            name of the EventType? Isn't it better to omit [organization:team] completely?)
     * @param owningApplication
     *            Indicator of the Application owning this `EventType`.
     * @param category
     *            Defines the category of this EventType. The value set will influence, if not set otherwise, the
     *            default set of validation-strategies, enrichment-strategies, and the effective_schema in the following
     *            way: - `undefined`: No predefined changes apply. `effective_schema` is exactly the same as the
     *            `EventTypeSchema`. Default validation_strategy for this `EventType` is `[{name:
     *            'schema-validation'}]`. - `data`: Events of this category will be DataChangeEvents. `effective_schema`
     *            contains `metadata`, and adds fields `data_op` and `data_type`. The passed EventTypeSchema defines the
     *            schema of `data`. Default validation_strategy for this `EventType` is `[{name:
     *            'datachange-schema-validation'}]`. - `business`: Events of this category will be BusinessEvents.
     *            `effective_schema` contains `metadata` and any additionally defined properties passed in the
     *            `EventTypeSchema` directly on top level of the Event. If name conflicts arise, creation of this
     *            EventType will be rejected. Default validation_strategy for this `EventType` is `[{name:
     *            'schema-validation'}]`.
     * @param validationStrategies
     *            Determines the validation that has to be executed upon reception of Events of this type. Events are
     *            rejected if any of the rules fail (see details of Problem response on the Event publishing methods).
     *            Rule evaluation order is the same as in this array. If not explicitly set, default value will respect
     *            the definition of the `EventType.category`.
     * @param enrichmentStrategies
     *            Determines the enrichment to be performed on an Event upon reception. Enrichment is performed once
     *            upon reception (and after validation) of an Event and is only possible on fields that are not defined
     *            on the incoming Event. See documentation for the write operation for details on behaviour in case of
     *            unsuccessful enrichment.
     * @param partitionStrategy
     *            Determines how the assignment of the event to a Partition should be handled.
     * @param schema
     *            The schema for this EventType. This is expected to be a json schema in yaml format (other formats
     *            might be added in the future).
     * @param dataKeyFields
     *            Indicators of the path of the properties that constitute the primary key (identifier) of the data
     *            within this Event. If set MUST be a valid required field as defined in the schema. (TBD should be
     *            required? Is applicable only to both Business and DataChange Events?)
     * @param partitioningKeyFields
     *            Indicator of the field used for guaranteeing the ordering of Events of this type (used by the
     *            PartitionResolutionStrategy). If set MUST be a valid required field as defined in the schema.
     * @param statistics
     *            Statistics of this EventType used for optimization purposes. Internal use of these values might change
     *            over time. (TBD: measured statistics).
     *
     */
    @JsonCreator
    public EventType(@JsonProperty("name") String name, @JsonProperty("owning_application") String owningApplication, @JsonProperty("category") EventTypeCategory category,
            @JsonProperty("validation_strategies") List<EventValidationStrategy> validationStrategies, @JsonProperty("enrichment_strategies") List<EventEnrichmentStrategy> enrichmentStrategies,
            @JsonProperty("partition_strategy") PartitionStrategy partitionStrategy, @JsonProperty("schema") EventTypeSchema schema, @JsonProperty("data_key_fields") List<String> dataKeyFields,
            @JsonProperty("partition_key_fields") List<String> partitionKeyFields, @JsonProperty("default_statistics") EventTypeStatistics statistics) {
        this.name = name;
        this.owningApplication = owningApplication;
        this.category = category;
        this.validationStrategies = validationStrategies;
        this.enrichmentStrategies = enrichmentStrategies;
        this.partitionStrategy = partitionStrategy;
        this.schema = schema;
        this.dataKeyFields = dataKeyFields;
        this.partitionKeyFields = partitionKeyFields;
        this.statistics = statistics;
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

    public List<EventValidationStrategy> getValidationStrategies() {
        return Collections.unmodifiableList(validationStrategies);
    }

    public List<EventEnrichmentStrategy> getEnrichmentStrategies() {
        return  Collections.unmodifiableList(enrichmentStrategies);
    }

    public PartitionStrategy getPartitionStrategy() {
        return partitionStrategy;
    }

    public EventTypeSchema getSchema() {
        return schema;
    }

    public List<String> getDataKeyFields() {
        return  Collections.unmodifiableList(dataKeyFields);
    }

    public List<String> getPartitionKeyFields() {
        return  Collections.unmodifiableList(partitionKeyFields);
    }

    public EventTypeStatistics getStatistics() {
        return statistics;
    }

    @Override
    public String toString() {
        return "EventType [name=" + name + ", owningApplication=" + owningApplication + ", category=" + category + ", validationStrategies=" + validationStrategies + ", enrichmentStrategies="
                + enrichmentStrategies + ", partitionStrategy=" + partitionStrategy + ", schema=" + schema + ", dataKeyFields=" + dataKeyFields + ", partitionKeyFields=" + partitionKeyFields
                + ", statistics=" + statistics + "]";
    }

}
