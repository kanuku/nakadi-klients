package org.zalando.nakadi.client.examples.java;

import java.util.ArrayList;
import java.util.List;

import org.zalando.nakadi.client.java.Client;
import org.zalando.nakadi.client.java.model.Event;
import org.zalando.nakadi.client.java.model.EventEnrichmentStrategy;
import org.zalando.nakadi.client.java.model.EventType;
import org.zalando.nakadi.client.java.model.EventTypeCategory;
import org.zalando.nakadi.client.java.model.EventTypeSchema;
import org.zalando.nakadi.client.java.model.EventValidationStrategy;
import org.zalando.nakadi.client.java.model.PartitionStrategy;
import org.zalando.nakadi.client.java.model.SchemaType;
import org.zalando.nakadi.client.scala.ClientFactory;
import org.zalando.nakadi.client.utils.ClientBuilder;

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

	public EventType createEventType(String name,EventTypeSchema eventTypeSchema) {
		String owner = "team-laas";
		EventTypeCategory category = EventTypeCategory.UNDEFINED;
		EventValidationStrategy validationStrategies = EventValidationStrategy.NONE;
		EventEnrichmentStrategy enrichmentStrategies = null;
		PartitionStrategy partitionStrategy = PartitionStrategy.RANDOM;
		 List<String> paritionKeyFields  = new ArrayList("date", "topic");
		 
		return new EventType(name,// eventTypeName
				owner,// owner
				category,// category
				validationStrategies,// validationStrategies
				enrichmentStrategies,// enrichmentStrategies
				partitionStrategy,// partitionStrategy
				eventTypeSchema,// eventTypeSchema
				null,// dataKeyFields
				null,// partitionKeyFields
				null);// statistics

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
