package org.zalando.nakadi.client.java;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

import org.zalando.nakadi.client.Serializer;
import org.zalando.nakadi.client.model.Event;
import org.zalando.nakadi.client.model.EventEnrichmentStrategy;
import org.zalando.nakadi.client.model.EventType;
import org.zalando.nakadi.client.model.EventValidationStrategy;
import org.zalando.nakadi.client.model.Metrics;
import org.zalando.nakadi.client.model.Partition;
import org.zalando.nakadi.client.model.PartitionStrategy;

import com.fasterxml.jackson.core.type.TypeReference;

public interface Client {

    /**
     * Retrieves monitoring metrics. NOTE: metrics format is v
     * 
     * @return metrics data
     */
    Future<Optional<Metrics>> getMetrics();

    /**
     * Retrieves all registered EventTypes.
     * 
     * @return List of known EventTypes
     */
    Future<Optional<List<EventType>>> getEventTypes();

    /**
     * Creates an eventType(topic).
     * 
     * @param eventType The EventType to create
     * @return Void in case of success
     */
    Future<Void> createEventType(EventType eventType);

    /**
     * Retrieves the EventType.
     * 
     * @param eventTypeName The unique name (id) of the EventType to retrieve
     * @return The EventType if it can be found  
     */
    Future<Optional<EventType>> getEventType(String eventTypeName);

    /**
     * Updates the eventType.
     * @param eventTypeName The unique name (id) of the EventType to update
     * @param eventType The eventType to be updated.
     * @return Void in case of success
     */
    Future<Void> updateEventType(String eventTypeName, EventType eventType);

    /**
     * Deletes an eventType.
     * @param eventTypeName The unique name (id) of the EventType to update
     * @return Void in case of success
     */
    Future<Void> deleteEventType(String eventTypeName);
    
    /**
     * Publishes a single event to the given eventType using a custom serializer. <br>
     * Partition selection is done using the defined partition resolution, <br>
     * which is defined per topic and managed by the event store.  
     * @param eventTypeName The unique name (id) of the EventType target 
     * @param event The event to be published
     * @param serializer The custom serializer to serialize the event.
     * @return Void in case of success
     */
    <T extends Event> Future<Void> publishEvent(String eventTypeName, T event, Serializer<T> serializer);
    
    /**
     * Publishes a single event to the given eventType. <br>
     * Partition selection is done using the defined partition resolution, <br>
     * which is defined per topic and managed by the event store.  
     * @param eventTypeName The unique name (id) of the EventType target 
     * @param event The event to be published
     * @param ref The Jackson TypeReference of the Event to be used by the default Jackson Marshaller.
     * @return Void in case of success     
     */
    <T extends Event> Future<Void> publishEvent(String eventTypeName, T event, TypeReference<T> ref);

    /**
     * Publishes a List of events to the given eventType using a custom serializer. <br>
     * Partition selection is done using the defined partition resolution, <br>
     * which is defined per topic and managed by the event store.  
     * @param eventTypeName The unique name (id) of the EventType target 
     * @param events The list of events to be published
     * @param serializer The custom serializer to serialize the events.
     * @return Void in case of success
     */
    <T extends Event> Future<Void> publishEvents(String eventTypeName, List<T> events, Serializer<List<T>> serializer);
    /**
     * Publishes a List of events to the given eventType. <br>
     * Partition selection is done using the defined partition resolution, <br>
     * which is defined per topic and managed by the event store.  
     * @param eventTypeName The unique name (id) of the EventType target 
     * @param event The event to be published
     * @param ref The Jackson TypeReference of the Event to be used by the default Jackson Marshaller.
     * @return Void in case of success     
     */
    <T extends Event> Future<Void> publishEvents(String eventTypeName, List<T> events, TypeReference<List<T>> ref);

    /**
     * Retrieves the existing partitions for the given EventType.
     * @param eventTypeName The unique name (id) of the EventType
     * @return list of existing partitions
     */
    Future<Optional<List<Partition>>> getPartitions(String eventTypeName);

    /**
     * Retrieves the unique partition, identified by the given parameters.  
     * @param eventTypeName The unique name (id) of the EventType
     * @param id The id of the partition
     * @return the partition if exists
     */
    Future<Optional<Partition>> getPartitionById(String eventTypeName, String id);

    /**
     * Retrieves a List of all Validation strategies supported by the Event store.
     * @return list of validation strategies
     */
    Future<Optional<List<EventValidationStrategy>>> getValidationStrategies();

    /**
     * Retrieves a List of all Enrichment strategies supported by the Event store.
     * @return list of enrichment strategies
     */
    Future<Optional<List<EventEnrichmentStrategy>>> getEnrichmentStrategies();
    /**
     * Retrieves a List of all Partition strategies supported by the Event store.
     * @return list of enrichment strategies
     */
    Future<Optional<List<PartitionStrategy>>> getPartitionStrategies();

    /**
     * Shuts down the communication system of the client
     * @return Void in case of success
     */
    Future<Void> stop();
}