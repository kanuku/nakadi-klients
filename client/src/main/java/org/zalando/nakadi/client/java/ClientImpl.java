package org.zalando.nakadi.client.java;

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
	
	 //Deserializers
	 private final Deserializer<Metrics> metricsDeserializer =Serialization.metricsDeserializer();
	 private final Deserializer<Partition> partitionDeserializer = Serialization.partitionDeserializer();
	 //List Deserializers
	 private final Deserializer<List<EventType>> seqOfEventTypeDeserializer =Serialization.seqOfEventTypeDeserializer();
	 private final Deserializer<List<Partition>> seqOfPartitionDeserializer =Serialization.seqOfPartitionDeserializer();
	 private final Deserializer<List<EventValidationStrategy>> seqOfEventValidationStrategy =Serialization.seqOfEventValidationStrategy();
	 private final Deserializer<List<EventEnrichmentStrategy>> seqOfEventEnrichmentStrategy =Serialization.seqOfEventEnrichmentStrategy(); 
	 private final Deserializer<List<PartitionStrategy>> seqOfPartitionStrategy =Serialization.seqOfPartitionStrategy();
	 //Serializers
	 private final Serializer<EventType> eventTypeSerializer =Serialization.defaultSerializer();
	 private final Deserializer<EventType> eventTypeDeserializer = Serialization.eventTypeDeserializer();

	public ClientImpl(Connection connection) {
		this.connection = connection;
	}

	@Override
	public Future<Optional<Metrics>> getMetrics() {

		return  connection.get4Java(Uri.URI_METRICS(), metricsDeserializer);
	}

	@Override
	public Future<Optional<List<EventType>>> getEventTypes() {

		return  connection.get4Java(Uri.URI_EVENT_TYPES(), seqOfEventTypeDeserializer);
	}

	@Override
	public Future<Void> createEventType(EventType eventType) {
		// TODO Auto-generated method stub
		return null;
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
			T event, Serializer<T> serializer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Event> Future<Void> publishEvent(String eventTypeName,
			T event) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Event> Future<Void> publishEvents(String eventTypeName,
			List<T> events, Serializer<T> serializer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Event> Future<Void> publishEvents(String eventTypeName,
			List<T> events) {
		// TODO Auto-generated method stub
		return null;
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

		return  connection.get4Java(Uri.URI_PARTITIONING_STRATEGIES(), seqOfPartitionStrategy);
	}

	@Override
	public Future<Void> stop() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Event> Future<Void> subscribe(String eventTypeName,
			org.zalando.nakadi.client.scala.StreamParameters parameters,
			Listener<T> listener, Deserializer<T> deserializer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Event> Future<Void> unsubscribe(String eventTypeName,
			Listener<T> listener) {
		// TODO Auto-generated method stub
		return null;
	}

	

}