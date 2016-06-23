package org.zalando.nakadi.client.java;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

import org.zalando.nakadi.client.Deserializer;
import org.zalando.nakadi.client.Serializer;
import org.zalando.nakadi.client.java.enumerator.EventEnrichmentStrategy;
import org.zalando.nakadi.client.java.enumerator.PartitionStrategy;
import org.zalando.nakadi.client.java.model.Event;
import org.zalando.nakadi.client.java.model.EventStreamBatch;
import org.zalando.nakadi.client.java.model.EventType;
import org.zalando.nakadi.client.java.model.Metrics;
import org.zalando.nakadi.client.java.model.Partition;

import com.fasterxml.jackson.core.type.TypeReference;

public interface Client {

    /**
     * Retrieves all metric data.
     * 
     * @return metrics data
     */
    Future<Optional<Metrics>> getMetrics();

    /**
     * Retrieves a list of all registered EventTypes.
     * 
     * @return List of known EventTypes
     */
    Future<Optional<List<EventType>>> getEventTypes();

    /**
     * Creates a new `EventType`.
     * 
     * @param eventType
     *            The EventType to create
     * @return Void in case of success
     */
    Future<Void> createEventType(EventType eventType);

    /**
     * Retrieves the EventType identified by its name.
     * 
     * @param eventTypeName
     *            The name of the EventType to retrieve.
     * @return The EventType if it can be found
     */
    Future<Optional<EventType>> getEventType(String eventTypeName);

    /**
     * Updates the eventType identified by its name.
     * 
     * @param eventTypeName
     *            The name of the EventType to update.
     * @param eventType
     *            The eventType to update.
     * @return Void in case of success
     */
    Future<Void> updateEventType(String eventTypeName, EventType eventType);

    /**
     * Deletes an eventType identified by its name.
     * 
     * @param eventTypeName
     *            The name of the EventType to delete.
     * @return Void in case of success
     */
    Future<Void> deleteEventType(String eventTypeName);

    /**
     * Publishes a single event to the given eventType using a custom serializer. <br>
     * 
     * @param eventTypeName
     *            The name of the EventType target.
     * @param event
     *            The event to publish.
     * @param serializer
     *            The serializer to use for the serialization of the event.
     * @return Void in case of success
     */
    <T extends Event> Future<Void> publishEvent(String eventTypeName, T event, Serializer<List<T>> serializer);

    /**
     * Publishes a single event to the given eventType identified by its name. <br>
     * 
     * @param eventTypeName
     *            The name of the EventType target.
     * @param event
     *            The event to publish
     * @param ref
     *            The Jackson TypeReference of the Event to be used by the default Jackson Marshaller.
     * @return Void in case of success
     */
    <T extends Event> Future<Void> publishEvent(String eventTypeName, T event);

    /**
     * Publishes a Batch(list) of events to the given eventType, identified by its name, using a custom serializer.
     * 
     * @param eventTypeName
     *            The name of the EventType target.
     * @param events
     *            The list of events to be published
     * @param serializer
     *            The custom serializer to serialize the events.
     * @return Void in case of success
     */
    <T extends Event> Future<Void> publishEvents(String eventTypeName, List<T> events, Serializer<List<T>> serializer);

    /**
     * Publishes a List of events to the given eventType identified by its name.
     * 
     * @param eventTypeName
     *            The name of the EventType target.
     * @param event
     *            The event to publish
     * @return Void in case of success
     */
    <T extends Event> Future<Void> publishEvents(String eventTypeName, List<T> events);

    /**
     * Retrieves the existing partitions for the given EventType.
     * 
     * @param eventTypeName
     *            The name of the EventType target.
     * @return list of existing partitions
     */
    Future<Optional<List<Partition>>> getPartitions(String eventTypeName);

    /**
     * Retrieves a List of all enrichment strategies supported.
     * 
     * @return list of enrichment strategies
     */
    Future<Optional<List<EventEnrichmentStrategy>>> getEnrichmentStrategies();

    /**
     * Retrieves a List of all partition strategies supported.
     * 
     * @return list of enrichment strategies
     */
    Future<Optional<List<PartitionStrategy>>> getPartitioningStrategies();

    /**
     * Shuts down the communication system of the client
     * 
     */
    void stop();

    /**
     * Subscribes a listener to the eventType, identified by its name, and start streaming events in a non-blocking
     * fashion.
     * 
     * @param eventTypeName
     *            The name of the EventType target.
     * @param parameters
     *            Parameters for customizing the details of the streaming.
     * @param listener
     *            Listener to pass the event to when it is received.
     * @param deserializer
     *            Deserializer to use for deserializing events.
     * @return ClientError in case of failure and Empty Optional in case of success.
     */
    <T extends Event> Optional<ClientError> subscribe(String eventTypeName, StreamParameters parameters, Listener<T> listener, Deserializer<EventStreamBatch<T>> deserializer);

    /**
     * Subscribes a listener to the eventType, identified by its name, and start streaming events in a non-blocking
     * fashion.
     * 
     * @param eventTypeName
     *            The name of the EventType target.
     * @param parameters
     *            Parameters for customizing the details of the streaming.
     * @param listener
     *            Listener to pass the event to when it is received.
     * @param typeRef
     *            TypeReference for unmarshalling with the Jackson ObjectMapper.
     * @return ClientError in case of failure and Empty Optional in case of success.
     */
    <T extends Event> Optional<ClientError> subscribe(String eventTypeName, StreamParameters parameters, Listener<T> listener, TypeReference<EventStreamBatch<T>> typeRef);

    /**
     * Removes the subscription of a listener, to stop streaming events from a partition.
     * 
     * @param eventTypeName
     *           The name of the EventType target.
     * @param partition
     *            The partition assigned to this listener.
     * @param listener
     *            Listener to pass the event to when it is received.
     * @return Void in case of success
     */
    <T extends Event> void unsubscribe(String eventTypeName, Optional<String> partition, Listener<T> listener);
}