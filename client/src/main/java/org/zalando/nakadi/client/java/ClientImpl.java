package org.zalando.nakadi.client.java;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import org.zalando.nakadi.client.Serializer;
import org.zalando.nakadi.client.model.Event;
import org.zalando.nakadi.client.model.EventEnrichmentStrategy;
import org.zalando.nakadi.client.model.EventType;
import org.zalando.nakadi.client.model.EventValidationStrategy;
import org.zalando.nakadi.client.model.Metrics;
import org.zalando.nakadi.client.model.Partition;
import org.zalando.nakadi.client.model.PartitionStrategy;
import org.zalando.nakadi.client.scala.ClientBuilder;
import org.zalando.nakadi.client.utils.FutureConversions;
import org.zalando.nakadi.client.utils.Serialization;

import com.fasterxml.jackson.core.type.TypeReference;

public class ClientImpl implements Client {
	private final org.zalando.nakadi.client.scala.Client client;
	
	
	
	public ClientImpl(org.zalando.nakadi.client.scala.Client client) {
		this.client = client;
	}

	public ClientImpl(String host, //
			Optional<Integer> port, //
			Optional<Supplier<String>> provider, //
			Optional<Boolean> securedConnection, //
			Optional<Boolean> verifySSlCertificate) {

		ClientBuilder builder = new ClientBuilder().withHost(host);
		if (port.isPresent())
			builder = builder.withPort(port.get());
		if (provider.isPresent())
			builder = builder.withTokenProvider4Java(provider.get());
		if (securedConnection.isPresent())
			builder = builder.withSecuredConnection(securedConnection.get());
		if (verifySSlCertificate.isPresent())
			builder = builder.withVerifiedSslCertificate(verifySSlCertificate
					.get());

		client = builder.build();
	}

	@Override
	public Future<Optional<Metrics>> getMetrics() {
		return FutureConversions.fromOptionOfEither2Optional(client.getMetrics(Serialization.metricsDeserializer()));
	}

	@Override
	public Future<Optional<List<EventType>>> getEventTypes() {
		return FutureConversions.fromSeqOfOptionalEither2OptionalList(client.getEventTypes(Serialization.seqOfEventTypeDeserializer()));
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
			T event, TypeReference<T> ref) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Event> Future<Void> publishEvents(String eventTypeName,
			List<T> events, Serializer<List<T>> serializer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Event> Future<Void> publishEvents(String eventTypeName,
			List<T> events, TypeReference<List<T>> ref) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Optional<List<Partition>>> getPartitions(String eventTypeName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Optional<Partition>> getPartitionById(String eventTypeName,
			String id) {
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
	public Future<Optional<List<PartitionStrategy>>> getPartitionStrategies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Void> stop() {
		// TODO Auto-generated method stub
		return null;
	}

}