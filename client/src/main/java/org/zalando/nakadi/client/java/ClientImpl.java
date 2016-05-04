package org.zalando.nakadi.client.java;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

import org.zalando.nakadi.client.Deserializer;
import org.zalando.nakadi.client.Serializer;
import org.zalando.nakadi.client.java.enumerator.EventEnrichmentStrategy;
import org.zalando.nakadi.client.java.enumerator.EventValidationStrategy;
import org.zalando.nakadi.client.java.enumerator.PartitionStrategy;
import org.zalando.nakadi.client.java.model.Cursor;
import org.zalando.nakadi.client.java.model.Event;
import org.zalando.nakadi.client.java.model.EventStreamBatch;
import org.zalando.nakadi.client.java.model.EventType;
import org.zalando.nakadi.client.java.model.Metrics;
import org.zalando.nakadi.client.java.model.Partition;
import org.zalando.nakadi.client.java.utils.SerializationUtils;
import org.zalando.nakadi.client.scala.Connection;
import org.zalando.nakadi.client.utils.Uri;

import com.fasterxml.jackson.core.type.TypeReference;

public class ClientImpl implements Client {
    private final Connection connection;

    // Deserializers
    private final Deserializer<Metrics> metricsDeserializer = SerializationUtils.metricsDeserializer();
    private final Deserializer<Partition> partitionDeserializer = SerializationUtils.partitionDeserializer();
    // List Deserializers
    private final Deserializer<List<EventType>> seqOfEventTypeDeserializer = SerializationUtils.seqOfEventTypeDeserializer();
    private final Deserializer<List<Partition>> seqOfPartitionDeserializer = SerializationUtils.seqOfPartitionDeserializer();
    private final Deserializer<List<EventValidationStrategy>> seqOfEventValidationStrategy = SerializationUtils
            .seqOfEventValidationStrategy();
    private final Deserializer<List<EventEnrichmentStrategy>> seqOfEventEnrichmentStrategy = SerializationUtils
            .seqOfEventEnrichmentStrategy();
    private final Deserializer<List<PartitionStrategy>> seqOfPartitionStrategy = SerializationUtils.seqOfPartitionStrategy();
    // Serializers
    private final Serializer<EventType> eventTypeSerializer = SerializationUtils.defaultSerializer();
    private final Deserializer<EventType> eventTypeDeserializer = SerializationUtils.eventTypeDeserializer();

    public ClientImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Future<Optional<Metrics>> getMetrics() {

        return connection.get4Java(Uri.URI_METRICS(), metricsDeserializer);
    }

    @Override
    public Future<Optional<List<EventType>>> getEventTypes() {

        return connection.get4Java(Uri.URI_EVENT_TYPES(), seqOfEventTypeDeserializer);
    }

    @Override
    public Future<Void> createEventType(EventType eventType) {
        return connection.post4Java(Uri.URI_EVENT_TYPES(), eventType, eventTypeSerializer);
    }

    @Override
    public Future<Optional<EventType>> getEventType(String eventTypeName) {
        return null;
    }

    @Override
    public Future<Void> updateEventType(String eventTypeName, EventType eventType) {
        return null;
    }

    @Override
    public Future<Void> deleteEventType(String eventTypeName) {
        return null;
    }

    @Override
    public <T extends Event> Future<Void> publishEvent(String eventTypeName, T event, Serializer<List<T>> serializer) {
        return publishEvents(eventTypeName, Arrays.asList(event), serializer);
    }

    @Override
    public <T extends Event> Future<Void> publishEvent(String eventTypeName, T event) {
        return publishEvents(eventTypeName, Arrays.asList(event));
    }

    @Override
    public <T extends Event> Future<Void> publishEvents(String eventTypeName, List<T> events, Serializer<List<T>> serializer) {
        return connection.post4Java(Uri.getEventStreamingUri(eventTypeName), events, serializer);
    }

    @Override
    public <T extends Event> Future<Void> publishEvents(String eventTypeName, List<T> events) {
        return publishEvents(eventTypeName, events, SerializationUtils.defaultSerializer());
    }

    @Override
    public Future<Optional<List<Partition>>> getPartitions(String eventTypeName) {
        return null;
    }

    @Override
    public Future<Optional<List<EventValidationStrategy>>> getValidationStrategies() {
        return null;
    }

    @Override
    public Future<Optional<List<EventEnrichmentStrategy>>> getEnrichmentStrategies() {
        return null;
    }

    @Override
    public Future<Optional<List<PartitionStrategy>>> getPartitioningStrategies() {

        return connection.get4Java(Uri.URI_PARTITIONING_STRATEGIES(), seqOfPartitionStrategy);
    }

    @Override
    public Future<Void> stop() {
        return null;
    }

    @Override
    public <T extends Event> Future<Void> subscribe(String eventTypeName, Optional<Cursor> cursor, Listener<T> listener,
            Deserializer<EventStreamBatch<T>> deserializer) {
        return connection.subscribeJava(Uri.getEventStreamingUri(eventTypeName), cursor, listener, deserializer);
    }

    @Override
    public <T extends Event> Future<Void> subscribe(String eventTypeName, Optional<Cursor> cursor, Listener<T> listener,
            TypeReference<EventStreamBatch<T>> typeRef) {
        return connection.subscribeJava(Uri.getEventStreamingUri(eventTypeName), cursor, listener, SerializationUtils.withCustomDeserializer(typeRef));
    }

    @Override
    public <T extends Event> Future<Void> unsubscribe(String eventTypeName, Listener<T> listener) {
        return null;
    }

}