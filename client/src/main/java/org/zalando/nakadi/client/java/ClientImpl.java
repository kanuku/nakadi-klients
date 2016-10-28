package org.zalando.nakadi.client.java;

import java.util.Arrays;
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
import org.zalando.nakadi.client.java.utils.SerializationUtils;
import org.zalando.nakadi.client.utils.Uri;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class ClientImpl implements Client {
    private final JavaClientHandler handler;

    // Deserializers
    private final Deserializer<Metrics> metricsDeserializer = SerializationUtils.metricsDeserializer();
    // List Deserializers
    private final Deserializer<List<EventType>> seqOfEventTypeDeserializer = SerializationUtils.seqOfEventTypeDeserializer();
    private final Deserializer<List<Partition>> seqOfPartitionDeserializer = SerializationUtils.seqOfPartitionDeserializer();
    private final Deserializer<List<EventEnrichmentStrategy>> seqOfEventEnrichmentStrategy = SerializationUtils.seqOfEventEnrichmentStrategy();
    private final Deserializer<List<PartitionStrategy>> seqOfPartitionStrategy = SerializationUtils.seqOfPartitionStrategy();
    // Serializers
    private final Serializer<EventType> eventTypeSerializer = SerializationUtils.defaultSerializer();
    private final Deserializer<EventType> eventTypeDeserializer = SerializationUtils.eventTypeDeserializer();

    public ClientImpl(JavaClientHandler handler) {
        this.handler = handler;
    }

    @Override
    public Future<Optional<Metrics>> getMetrics() {
        return handler.get(Uri.URI_METRICS(), metricsDeserializer);
    }

    @Override
    public Future<Optional<List<EventType>>> getEventTypes() {
        return handler.get(Uri.URI_EVENT_TYPES(), seqOfEventTypeDeserializer);
    }

    @Override
    public Future<Void> createEventType(EventType eventType) {
    	checkNotNull(eventType, "EventType may not be null");
        return handler.post(Uri.URI_EVENT_TYPES(), eventType, eventTypeSerializer);
    }

    @Override
    public Future<Optional<EventType>> getEventType(String eventTypeName) {
    	checkStringIsNotNullOrEmpty(eventTypeName,"eventTypeName may not be null or empty");
        return handler.get(Uri.getEventTypeByName(eventTypeName), eventTypeDeserializer);
    }

    @Override
    public Future<Void> updateEventType(String eventTypeName, EventType eventType) {
    	checkStringIsNotNullOrEmpty(eventTypeName,"eventTypeName may not be null or empty");
    	checkNotNull(eventType, "EventType may not be null");
       return handler.put(Uri.getEventTypeByName(eventTypeName), eventType, eventTypeSerializer);
    }

    @Override
    public Future<Void> deleteEventType(String eventTypeName) {
    	checkStringIsNotNullOrEmpty(eventTypeName,"eventTypeName may not be null or empty");
        return handler.delete(Uri.getEventTypeByName(eventTypeName));
    }

    @Override
    public <T extends Event> Future<Void> publishEvent(String eventTypeName, T event, Serializer<List<T>> serializer) {
    	checkNotNull(event, "Event may not be null");
        return publishEvents(eventTypeName, Arrays.asList(event), serializer);
    }

    @Override
    public <T extends Event> Future<Void> publishEvent(String eventTypeName, T event) {
    	checkStringIsNotNullOrEmpty(eventTypeName,"eventTypeName may not be null or empty");
    	checkNotNull(event, "Event may not be null");
        return publishEvents(eventTypeName, Arrays.asList(event));
    }

    @Override
    public <T extends Event> Future<Void> publishEvents(String eventTypeName, List<T> events,
            Serializer<List<T>> serializer) {
    	checkStringIsNotNullOrEmpty(eventTypeName,"eventTypeName may not be null or empty");
    	checkListIsNotEmptyOrNull(events, "List of events must not be null or empty");
        return handler.post(Uri.getEventStreamingUri(eventTypeName), events, serializer);
    }

    @Override
    public <T extends Event> Future<Void> publishEvents(String eventTypeName, List<T> events) {
    	checkListIsNotEmptyOrNull(events, "List of events must not be null or empty");
        return publishEvents(eventTypeName, events, SerializationUtils.defaultSerializer());
    }

    @Override
    public Future<Optional<List<Partition>>> getPartitions(String eventTypeName) {
    	checkStringIsNotNullOrEmpty(eventTypeName,"eventTypeName may not be null or empty");
        return handler.get(Uri.getPartitions(eventTypeName), seqOfPartitionDeserializer);
    }

    @Override
    public Future<Optional<List<EventEnrichmentStrategy>>> getEnrichmentStrategies() {
        return handler.get(Uri.URI_ENRICHMENT_STRATEGIES(), seqOfEventEnrichmentStrategy);
    }

    @Override
    public Future<Optional<List<PartitionStrategy>>> getPartitioningStrategies() {

        return handler.get(Uri.URI_PARTITIONING_STRATEGIES(), seqOfPartitionStrategy);
    }

    @Override
    public void stop() {
        handler.stop();
    }

    @Override
    public <T extends Event> Optional<ClientError> subscribe(String eventTypeName, StreamParameters parameters,
            Listener<T> listener, Deserializer<EventStreamBatch<T>> deserializer) {
    	checkStringIsNotNullOrEmpty(eventTypeName,"eventTypeName may not be null or empty");
        return handler.subscribe(eventTypeName, Uri.getEventStreamingUri(eventTypeName), parameters, listener,
                deserializer);
    }

    @Override
    public <T extends Event> Optional<ClientError> subscribe(String eventTypeName, StreamParameters parameters,
            Listener<T> listener, TypeReference<EventStreamBatch<T>> typeRef) {
    	checkStringIsNotNullOrEmpty(eventTypeName,"eventTypeName may not be null or empty");
    	checkNotNull(listener, "Listener may not be null");
        return handler.subscribe(eventTypeName, Uri.getEventStreamingUri(eventTypeName), parameters, listener,
                SerializationUtils.withCustomDeserializer(typeRef));
    }

    @Override
    public <T extends Event> void unsubscribe(String eventTypeName, Optional<String> partition, Listener<T> listener) {
    	checkStringIsNotNullOrEmpty(eventTypeName,"eventTypeName may not be null or empty");
    	checkNotNull(listener, "Listener may not be null");
        handler.unsubscribe(eventTypeName, partition, listener);
    }
    
    private <T> void checkListIsNotEmptyOrNull(List<T> l, String message){
    	Preconditions.checkArgument(l!=null&&!l.isEmpty(), message);
    	
    }
    private void checkStringIsNotNullOrEmpty(String in, String message){
    	Preconditions.checkArgument(!Strings.isNullOrEmpty(in), message);
    }
    private <T>void checkNotNull(T obj, String message){
    	Preconditions.checkNotNull(obj,message);
    }

}