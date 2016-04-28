package org.zalando.nakadi.client.examples.java;

import java.util.List;

import org.zalando.nakadi.client.java.Client;
import org.zalando.nakadi.client.java.enumerator.EventEnrichmentStrategy;
import org.zalando.nakadi.client.java.enumerator.EventTypeCategory;
import org.zalando.nakadi.client.java.enumerator.EventValidationStrategy;
import org.zalando.nakadi.client.java.enumerator.PartitionStrategy;
import org.zalando.nakadi.client.java.enumerator.SchemaType;
import org.zalando.nakadi.client.java.model.Event;
import org.zalando.nakadi.client.java.model.EventType;
import org.zalando.nakadi.client.java.model.EventTypeSchema;
import org.zalando.nakadi.client.java.model.EventTypeStatistics;
import org.zalando.nakadi.client.scala.ClientFactory;
import org.zalando.nakadi.client.utils.ClientBuilder;

import com.google.common.collect.Lists;

public class EventCreationExample {

	public class MeetingsEvent implements Event {
		private final String date;
		private final String topic;

		public MeetingsEvent(String date, String topic) {
			this.date = date;
			this.topic = topic;
		}

		public String getDate() {
			return date;
		}

		public String getTopic() {
			return topic;
		}

	}

	public Client createClient() {
		return new ClientBuilder()//
				.withHost("nakadi-sandbox.aruha-test.zalan.do")//
				.withSecuredConnection(true) // s
				.withVerifiedSslCertificate(false) // s
				.withTokenProvider4Java(() -> ClientFactory.getToken())//
				.buildJavaClient();
	}

	public EventTypeSchema createEventTypeSchema(String schema) {
		return new EventTypeSchema(SchemaType.JSON, schema);
	}

	public EventType createEventType(String name,
			EventTypeSchema eventTypeSchema) {
		String owningApplication = "team-laas";
		EventTypeCategory category = EventTypeCategory.UNDEFINED;
		List<EventValidationStrategy> validationStrategies = Lists
				.newArrayList(EventValidationStrategy.NONE);
		List<EventEnrichmentStrategy> enrichmentStrategies = Lists
				.newArrayList();
		PartitionStrategy partitionStrategy = PartitionStrategy.RANDOM;

		List<String> dataKeyFields = null;
		List<String> partitionKeyFields = Lists.newArrayList("date", "topic");
		EventTypeStatistics statistics = null;
		return new EventType(name, //
				owningApplication, //
				category, //
				validationStrategies, //
				enrichmentStrategies, //
				partitionStrategy, //
				eventTypeSchema, //
				dataKeyFields, //
				partitionKeyFields, //
				statistics);

	}

	public static void main(String[] args) {
		EventCreationExample example = new EventCreationExample();
		// 1. Create client
		final Client client = example.createClient();

		// 2. Create a simple Meeting instance
		EventCreationExample.MeetingsEvent meeting = example.new MeetingsEvent(
				"2016-04-28T13:28:15+00:00", "Hackthon");

		// Create the EventType
		String schema = " { \"properties\":\" { \"date\": { \"type\": \"string\" }, \"topic\": { \"type\": \"string\"} } }";

		EventTypeSchema eventSchame = example.createEventTypeSchema(schema);

	}
}
