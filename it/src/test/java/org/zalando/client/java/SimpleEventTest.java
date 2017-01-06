package org.zalando.client.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Test;
import org.zalando.nakadi.client.java.Client;
import org.zalando.nakadi.client.java.StreamParameters;
import org.zalando.nakadi.client.java.enumerator.PartitionStrategy;
import org.zalando.nakadi.client.java.model.Cursor;
import org.zalando.nakadi.client.java.model.Event;
import org.zalando.nakadi.client.java.model.EventStreamBatch;
import org.zalando.nakadi.client.java.model.EventType;
import org.zalando.nakadi.client.java.model.EventTypeStatistics;
import org.zalando.nakadi.client.java.test.event.generator.EventGenerator;
import org.zalando.nakadi.client.java.test.event.generator.EventIntegrationHelper;
import org.zalando.nakadi.client.java.test.event.simple.MySimpleEvent;
import org.zalando.nakadi.client.java.test.event.simple.MySimpleEventGenerator;
import org.zalando.nakadi.client.java.test.event.simple.SimpleEventListener;
import org.zalando.nakadi.client.scala.ClientFactory;

import com.fasterxml.jackson.core.type.TypeReference;

public class SimpleEventTest {

    private Client client = ClientFactory.buildJavaClient();
    private Integer nrOfEvents = 45;

    private TypeReference<EventStreamBatch<MySimpleEvent>> typeRef = new TypeReference<EventStreamBatch<MySimpleEvent>>() {
    };

    @After
    public void shutdown() throws InterruptedException, ExecutionException {
        client.stop();
    }

    @Test
    public void handle404Graciously() throws InterruptedException, ExecutionException {

        EventGenerator gen = new MySimpleEventGenerator()//
                .withEventTypeId("SimpleEventTest-handle404Graciously")//
                .build();
        EventIntegrationHelper it = new EventIntegrationHelper(gen, client);
        assertTrue(it.createEventType());
        assertTrue(it.getEventType().isPresent());
        Optional<EventType> result = client.getEventType("none-existing-event-type-name").get();
        assertEquals(result.isPresent(), false);
    }

    @Test
    public void validatePublishedNrOfEvents() throws InterruptedException, ExecutionException {
        SimpleEventListener listener = new SimpleEventListener();
        EventGenerator gen = new MySimpleEventGenerator()//
                .withEventTypeId("SimpleEventTest-validatePublishedNrOfEvents").build();
        EventIntegrationHelper it = new EventIntegrationHelper(gen, client);
        assertTrue("EventType should be created", it.createEventType());
        Thread.sleep(5000);// Creation can take time.
        Optional<EventType> eventTypeOpt = it.getEventType();
        assertTrue("Did not return the eventType", eventTypeOpt.isPresent());
        List<Event> createdEvents = it.publishEvents(nrOfEvents);
        Optional<Cursor> cursor = Optional.of(new Cursor("0", "BEGIN"));
        Optional<Integer> batchLimit = Optional.empty();
        Optional<Integer> streamLimit = Optional.empty();
        Optional<Integer> batchFlushTimeout = Optional.empty();
        Optional<Integer> streamTimeout = Optional.empty();
        Optional<Integer> streamKeepAliveLimit = Optional.empty();
        Optional<String> flowId = Optional.empty();
        StreamParameters parameters = new StreamParameters(cursor, //
                batchLimit, //
                streamLimit, //
                batchFlushTimeout, //
                streamTimeout, //
                streamKeepAliveLimit, //
                flowId);
        client.subscribe(it.getGen().getEventTypeName(), parameters, listener, typeRef);
        List<MySimpleEvent> receivedEvents = listener.waitToReceive(nrOfEvents);
        assertEquals("Created & Received events differ in number", createdEvents.size(), receivedEvents.size());
    }

    @Test
    public void unsubscribedListenerShouldNotReceiveAnyEvents() throws InterruptedException, ExecutionException {
        SimpleEventListener listener = new SimpleEventListener();
        EventGenerator gen = new MySimpleEventGenerator()//
                .withEventTypeId("SimpleEventTest-validatePublishedNrOfEvents").build();
        EventIntegrationHelper it = new EventIntegrationHelper(gen, client);
        assertTrue("EventType should be created", it.createEventType());
        Thread.sleep(1000);// Creation can take time.
        Optional<EventType> eventTypeOpt = it.getEventType();
        assertTrue("Did not return the eventType", eventTypeOpt.isPresent());

        Optional<Cursor> cursor = Optional.of(new Cursor("0", "BEGIN"));
        Optional<Integer> batchLimit = Optional.empty();
        Optional<Integer> streamLimit = Optional.empty();
        Optional<Integer> batchFlushTimeout = Optional.empty();
        Optional<Integer> streamTimeout = Optional.empty();
        Optional<Integer> streamKeepAliveLimit = Optional.empty();
        Optional<String> flowId = Optional.empty();
        StreamParameters parameters = new StreamParameters(cursor, //
                batchLimit, //
                streamLimit, //
                batchFlushTimeout, //
                streamTimeout, //
                streamKeepAliveLimit, //
                flowId);
        client.subscribe(it.getGen().getEventTypeName(), parameters, listener, typeRef);
        client.unsubscribe(it.getGen().getEventTypeName(), Optional.of("0"), listener);
        it.publishEvents(nrOfEvents);
        Thread.sleep(5000);
        assertEquals("Unsubscribed listener must not receive events", 0, listener.getReceivedEvents().size());
    }

    @Test
    public void validateCreatedEventType() throws InterruptedException {
        EventGenerator gen = new MySimpleEventGenerator()//
                .withEventTypeId("SimpleEventTest-validateCreatedEventType").build();
        EventIntegrationHelper it = new EventIntegrationHelper(gen, client);
        assertTrue("EventType should be created", it.createEventType());
        Thread.sleep(1000);// Creation can take time.
        Optional<EventType> eventTypeOpt = it.getEventType();
        assertTrue("Did not return the eventType", eventTypeOpt.isPresent());

        EventType originalEventType = it.getEventType().get();
        EventType eventType = eventTypeOpt.get();
        assertEquals(eventType.getCategory(), originalEventType.getCategory());
        assertEquals(eventType.getDataKeyFields(), originalEventType.getDataKeyFields());
        assertEquals(eventType.getName(), originalEventType.getName());
        assertEquals(eventType.getOwningApplication(), originalEventType.getOwningApplication());
        assertEquals(eventType.getPartitionKeyFields(), originalEventType.getPartitionKeyFields());
        assertEquals(eventType.getPartitionStrategy(), originalEventType.getPartitionStrategy());
        assertEquals(eventType.getSchema().getSchema(), originalEventType.getSchema().getSchema());
        assertEquals(eventType.getSchema().getType(), originalEventType.getSchema().getType());
        assertNull(eventType.getStatistics());
    }
    @Test
    public void validateCreatedEventTypeWithStatistics() throws InterruptedException {
        Integer messagesPerMinute = 2400;
        Integer messageSize = 20240;
        Integer readParallelism = 8;
        Integer writeParallelism = 7;
        EventGenerator gen = new MySimpleEventGenerator(){
            @Override
          public EventTypeStatistics getStatistics() {
            return new EventTypeStatistics(messagesPerMinute, messageSize, readParallelism, writeParallelism);
          }
        }
        .withEventTypeId("SimpleEventTest-validateCreatedEventType").build();
        EventIntegrationHelper it = new EventIntegrationHelper(gen, client);
        assertTrue("EventType should be created", it.createEventType());
        Thread.sleep(1000);// Creation can take time.
        Optional<EventType> eventTypeOpt = it.getEventType();
        assertTrue("Did not return the eventType", eventTypeOpt.isPresent());
        
        EventType originalEventType = it.getEventType().get();
        EventType eventType = eventTypeOpt.get();
        assertEquals(eventType.getCategory(), originalEventType.getCategory());
        assertEquals(eventType.getDataKeyFields(), originalEventType.getDataKeyFields());
        assertEquals(eventType.getName(), originalEventType.getName());
        assertEquals(eventType.getOwningApplication(), originalEventType.getOwningApplication());
        assertEquals(eventType.getPartitionKeyFields(), originalEventType.getPartitionKeyFields());
        assertEquals(eventType.getPartitionStrategy(), originalEventType.getPartitionStrategy());
        assertEquals(eventType.getSchema().getSchema(), originalEventType.getSchema().getSchema());
        assertEquals(eventType.getSchema().getType(), originalEventType.getSchema().getType());
        assertNotNull(eventType.getStatistics());
        assertEquals(messageSize, originalEventType.getStatistics().getMessageSize());
        assertEquals(messagesPerMinute, originalEventType.getStatistics().getMessagesPerMinute());
        assertEquals(readParallelism, originalEventType.getStatistics().getReadParallelism());
        assertEquals(writeParallelism, originalEventType.getStatistics().getWriteParallelism());
        assertEquals(eventType.getStatistics().getMessageSize(), originalEventType.getStatistics().getMessageSize());
        assertEquals(eventType.getStatistics().getMessagesPerMinute(), originalEventType.getStatistics().getMessagesPerMinute());
        assertEquals(eventType.getStatistics().getReadParallelism(), originalEventType.getStatistics().getReadParallelism());
        assertEquals(eventType.getStatistics().getWriteParallelism(), originalEventType.getStatistics().getWriteParallelism());
    }

    @Test
    public void validateNrOfPartition() throws InterruptedException, ExecutionException {
        EventGenerator gen = new MySimpleEventGenerator()//
                .withEventTypeId("SimpleEventTest-validateNrOfPartition").build();
        EventIntegrationHelper it = new EventIntegrationHelper(gen, client);
        assertTrue("EventType should be created", it.createEventType());
        assertEquals(it.getNumberOfPartitions(), Integer.valueOf(1));

    }

    @Test
    public void receivePartitionStrategies() throws InterruptedException, ExecutionException {
        EventGenerator gen = new MySimpleEventGenerator()//
                .withEventTypeId("SimpleEventTest-receivePartitionStrategies").build();
        EventIntegrationHelper it = new EventIntegrationHelper(gen, client);
        List<PartitionStrategy> strategies = it.getPartitionStrategies();
        assertEquals(strategies.size(), 3);
        for (PartitionStrategy ps : strategies) {
            assertTrue("Did not find PartitionStrategy:" + ps.name(), PartitionStrategy.withName(ps.name()).isPresent());

        }
    }

}
