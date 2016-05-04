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
import org.zalando.nakadi.client.java.model.Event;
import org.zalando.nakadi.client.java.model.EventType;
import org.zalando.nakadi.client.java.model.Metrics;
import org.zalando.nakadi.client.java.model.Partition;
import org.zalando.nakadi.client.scala.Connection;
import org.zalando.nakadi.client.utils.Serialization;
import org.zalando.nakadi.client.utils.Uri;

public class ClientImpl implements Client {
	private final Connection connection;

	// Deserializers
	private final Deserializer<Metrics> metricsDeserializer = Serialization
			.metricsDeserializer();
	private final Deserializer<Partition> partitionDeserializer = Serialization
			.partitionDeserializer();
	// List Deserializers
	private final Deserializer<List<EventType>> seqOfEventTypeDeserializer = Serialization
			.seqOfEventTypeDeserializer();
	private final Deserializer<List<Partition>> seqOfPartitionDeserializer = Serialization
			.seqOfPartitionDeserializer();
	private final Deserializer<List<EventValidationStrategy>> seqOfEventValidationStrategy = Serialization
			.seqOfEventValidationStrategy();
	private final Deserializer<List<EventEnrichmentStrategy>> seqOfEventEnrichmentStrategy = Serialization
			.seqOfEventEnrichmentStrategy();
	private final Deserializer<List<PartitionStrategy>> seqOfPartitionStrategy = Serialization
			.seqOfPartitionStrategy();
	// Serializers
	private final Serializer<EventType> eventTypeSerializer = Serialization
			.defaultSerializer();
	private final Deserializer<EventType> eventTypeDeserializer = Serialization
			.eventTypeDeserializer();

	public ClientImpl(Connection connection) {
		this.connection = connection;
	}

	@Override
	public Future<Optional<Metrics>> getMetrics() {

		return connection.get4Java(Uri.URI_METRICS(), metricsDeserializer);
	}

	@Override
	public Future<Optional<List<EventType>>> getEventTypes() {

		return connection.get4Java(Uri.URI_EVENT_TYPES(),
				seqOfEventTypeDeserializer);
	}

	@Override
	public Future<Void> createEventType(EventType eventType) {
		return connection.post4Java(Uri.URI_EVENT_TYPES(), eventType,
				eventTypeSerializer);
	}

	@Override
	public Future<Optional<EventType>> getEventType(String eventTypeName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Void> updateEventType(String eventTypeName,
			EventType eventType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Void> deleteEventType(String eventTypeName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Event> Future<Void> publishEvent(String eventTypeName,
			T event, Serializer<List<T>> serializer) {
		return publishEvents(eventTypeName, Arrays.asList(event), serializer);
	}

	@Override
	public <T extends Event> Future<Void> publishEvent(String eventTypeName,
			T event) {
		return publishEvents(eventTypeName, Arrays.asList(event));
	}

	@Override
	public <T extends Event> Future<Void> publishEvents(String eventTypeName,
			List<T> events, Serializer<List<T>> serializer) {
		return connection.post4Java(Uri.getEventStreamingUri(eventTypeName),
				events, serializer);
	}

	@Override
	public <T extends Event> Future<Void> publishEvents(String eventTypeName,
			List<T> events) {
		return publishEvents(eventTypeName, events,
				Serialization.defaultSerializer());
	}

	@Override
	public Future<Optional<List<Partition>>> getPartitions(String eventTypeName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Optional<List<EventValidationStrategy>>> getValidationStrategies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Optional<List<EventEnrichmentStrategy>>> getEnrichmentStrategies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Optional<List<PartitionStrategy>>> getPartitioningStrategies() {

		return connection.get4Java(Uri.URI_PARTITIONING_STRATEGIES(),
				seqOfPartitionStrategy);
	}

	@Override
	public Future<Void> stop() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Event> Future<Void> subscribe(String eventTypeName,
			StreamParameters parameters,
			Listener<T> listener, Deserializer<T> deserializer) {
		// TODO Auto-generated method stub
		return null;//connection.subscribeJava(eventTypeName, request, listener, des);;
	}

	@Override
	public <T extends Event> Future<Void> unsubscribe(String eventTypeName,
			Listener<T> listener) {
		// TODO Auto-generated method stub
		return null;
	}

}