package org.zalando.nakadi.client.java;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

import scala.collection.JavaConverters;

import org.zalando.nakadi.client.Deserializer;
import org.zalando.nakadi.client.java.Listener;
import org.zalando.nakadi.client.Serializer;
import org.zalando.nakadi.client.java.StreamParameters;
import org.zalando.nakadi.client.java.model.*;
import org.zalando.nakadi.client.java.enumerator.*;
import org.zalando.nakadi.client.utils.FutureConversions;
import org.zalando.nakadi.client.utils.Serialization;

import scala.collection.Seq;


public class ClientImpl implements Client {
	private final org.zalando.nakadi.client.scala.Client client;
	
//	//Deserializers
//	private final Deserializer<Metrics> metricsDeserializer =Serialization.metricsDeserializer();
//	private final Deserializer<Partition> partitionDeserializer = Serialization.partitionDeserializer();
//	//Seq Deserializers
//	private final Deserializer<Seq<EventType>> seqOfEventTypeDeserializer =Serialization.seqOfEventTypeDeserializer();
//	private final Deserializer<Seq<Partition>> seqOfPartitionDeserializer =Serialization.seqOfPartitionDeserializer();
//	private final Deserializer<Seq<EventValidationStrategy>> seqOfEventValidationStrategy =Serialization.seqOfEventValidationStrategy();
//	private final Deserializer<Seq<EventEnrichmentStrategy>> seqOfEventEnrichmentStrategy =Serialization.seqOfEventEnrichmentStrategy();
//	private final Deserializer<Seq<PartitionStrategy>> seqOfPartitionStrategy =Serialization.seqOfPartitionStrategy();
//	//Serializers
//	private final Serializer<EventType> eventTypeSerializer =Serialization.defaultSerializer();
//	private final Deserializer<EventType> eventTypeDeserializer = Serialization.eventTypeDeserializer();
	
	
	public ClientImpl(org.zalando.nakadi.client.scala.Client client) {
		this.client = client;
	}

@Override
public Future<Optional<Metrics>> getMetrics() {
    
    return null;
}

@Override
public Future<Optional<List<EventType>>> getEventTypes() {
    // TODO Auto-generated method stub
    return null;
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
public Future<Void> updateEventType(String eventTypeName, EventType eventType) {
    // TODO Auto-generated method stub
    return null;
}

@Override
public Future<Void> deleteEventType(String eventTypeName) {
    // TODO Auto-generated method stub
    return null;
}

@Override
public <T extends Event> Future<Void> publishEvent(String eventTypeName, T event, Serializer<T> serializer) {
    // TODO Auto-generated method stub
    return null;
}

@Override
public <T extends Event> Future<Void> publishEvent(String eventTypeName, T event) {
    // TODO Auto-generated method stub
    return null;
}

@Override
public <T extends Event> Future<Void> publishEvents(String eventTypeName, List<T> events, Serializer<T> serializer) {
    // TODO Auto-generated method stub
    return null;
}

@Override
public <T extends Event> Future<Void> publishEvents(String eventTypeName, List<T> events) {
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
    // TODO Auto-generated method stub
    return null;
}

@Override
public Future<Void> stop() {
    // TODO Auto-generated method stub
    return null;
}

@Override
public <T extends Event> Future<Void> subscribe(String eventTypeName,
        org.zalando.nakadi.client.scala.StreamParameters parameters, Listener<T> listener, Deserializer<T> deserializer) {
    // TODO Auto-generated method stub
    return null;
}

@Override
public <T extends Event> Future<Void> unsubscribe(String eventTypeName, Listener<T> listener) {
    // TODO Auto-generated method stub
    return null;
}

 
	 

}