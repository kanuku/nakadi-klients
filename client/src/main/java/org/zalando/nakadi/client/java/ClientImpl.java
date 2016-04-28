package org.zalando.nakadi.client.java;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

import scala.collection.JavaConverters;

import org.zalando.nakadi.client.Deserializer;
import org.zalando.nakadi.client.Listener;
import org.zalando.nakadi.client.Serializer;
import org.zalando.nakadi.client.StreamParameters;
import org.zalando.nakadi.client.java.model.Event;
import org.zalando.nakadi.client.java.model.EventEnrichmentStrategy;
import org.zalando.nakadi.client.java.model.EventType;
import org.zalando.nakadi.client.java.model.EventValidationStrategy;
import org.zalando.nakadi.client.java.model.Metrics;
import org.zalando.nakadi.client.java.model.Partition;
import org.zalando.nakadi.client.java.model.PartitionStrategy;
import org.zalando.nakadi.client.utils.FutureConversions;
import org.zalando.nakadi.client.utils.Serialization;

import scala.collection.Seq;


public class ClientImpl implements Client {
	private final org.zalando.nakadi.client.scala.Client client;
	
	//Deserializers
	private final Deserializer<Metrics> metricsDeserializer =Serialization.metricsDeserializer();
	private final Deserializer<Partition> partitionDeserializer = Serialization.partitionDeserializer();
	//Seq Deserializers
	private final Deserializer<Seq<EventType>> seqOfEventTypeDeserializer =Serialization.seqOfEventTypeDeserializer();
	private final Deserializer<Seq<Partition>> seqOfPartitionDeserializer =Serialization.seqOfPartitionDeserializer();
	private final Deserializer<Seq<EventValidationStrategy>> seqOfEventValidationStrategy =Serialization.seqOfEventValidationStrategy();
	private final Deserializer<Seq<EventEnrichmentStrategy>> seqOfEventEnrichmentStrategy =Serialization.seqOfEventEnrichmentStrategy();
	private final Deserializer<Seq<PartitionStrategy>> seqOfPartitionStrategy =Serialization.seqOfPartitionStrategy();
	//Serializers
	private final Serializer<EventType> eventTypeSerializer =Serialization.defaultSerializer();
	private final Deserializer<EventType> eventTypeDeserializer = Serialization.eventTypeDeserializer();
	
	
	public ClientImpl(org.zalando.nakadi.client.scala.Client client) {
		this.client = client;
	}

	 

	@Override
	public Future<Optional<Metrics>> getMetrics() {
		return FutureConversions.fromOptionOfEither2Optional(client.getMetrics(metricsDeserializer));
	}

	@Override
	public Future<Optional<List<EventType>>> getEventTypes() {
		return FutureConversions.fromSeqOfOptionalEither2OptionalList(client.getEventTypes(seqOfEventTypeDeserializer));
	}

	@Override
	public Future<Void> createEventType(EventType eventType) {
		
		return FutureConversions.fromOptional2Future(client.createEventType(eventType,eventTypeSerializer));
	}

	@Override
	public Future<Optional<EventType>> getEventType(String eventTypeName) {
		return FutureConversions.fromOptionOfEither2Optional(client.getEventType(eventTypeName,eventTypeDeserializer));
	}

	@Override
	public Future<Void> updateEventType(String eventTypeName,EventType eventType) {
		return  FutureConversions.fromOptional2Future(client.updateEventType(eventTypeName,eventType,eventTypeSerializer));
	}

	@Override
	public Future<Void> deleteEventType(String eventTypeName) {
		return  FutureConversions.fromOptional2Future(client.deleteEventType(eventTypeName));
	}

	@Override
	public <T extends Event> Future<Void> publishEvent(String eventTypeName, T event, Serializer<T> serializer) {
		return FutureConversions.fromOptional2Future(client.publishEvent(eventTypeName,event,serializer));
	}

	@Override
	public <T extends Event> Future<Void> publishEvent(String eventTypeName, T event) {
		Serializer<T> serializer =Serialization.defaultSerializer();
		return FutureConversions.fromOptional2Future(client.publishEvent(eventTypeName,event,serializer));
	}

	@Override
	public <T extends Event> Future<Void> publishEvents(String eventTypeName, List<T> events, Serializer<T> serializer) {
		return FutureConversions.fromOptional2Future(client.publishEvents(eventTypeName,events,serializer));
	}

	@Override
	public <T extends Event> Future<Void> publishEvents(String eventTypeName, List<T> events) {
		Serializer<T> serializer =Serialization.defaultSerializer();
		return FutureConversions.fromOptional2Future(client.publishEvents(eventTypeName,events,serializer));
	}

	@Override
	public Future<Optional<List<Partition>>> getPartitions(String eventTypeName) {
		return FutureConversions.fromSeqOfOptionalEither2OptionalList(client.getPartitions(eventTypeName,seqOfPartitionDeserializer));
	}

	@Override
	public Future<Optional<List<EventValidationStrategy>>> getValidationStrategies() {
		return FutureConversions.fromSeqOfOptionalEither2OptionalList(client.getValidationStrategies(seqOfEventValidationStrategy));
	}

	@Override
	public Future<Optional<List<EventEnrichmentStrategy>>> getEnrichmentStrategies() {
		return FutureConversions.fromSeqOfOptionalEither2OptionalList(client.getEnrichmentStrategies(seqOfEventEnrichmentStrategy));
	}

	@Override
	public Future<Optional<List<PartitionStrategy>>> getPartitioningStrategies() {
		return FutureConversions.fromSeqOfOptionalEither2OptionalList(client.getPartitioningStrategies(seqOfPartitionStrategy));
	}

	@Override
	public Future<Void> stop() {
		return FutureConversions.fromOptional2Future(client.stop());
	}
	@Override
	public <T extends Event> Future<Void> subscribe(String eventTypeName, StreamParameters parameters, Listener<T> listener, Deserializer<T> deserializer) {
		return FutureConversions.fromOptional2Future(client.subscribe(eventTypeName,parameters,listener,deserializer));
	}

	@Override
	public <T extends Event> Future<Void> unsubscribe(String eventTypeName, Listener<T> listener) {
		return FutureConversions.fromOptional2Future(client.unsubscribe(eventTypeName,listener));
	}

}