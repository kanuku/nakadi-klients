package org.zalando.client.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Test;
import org.zalando.nakadi.client.java.Client;
import org.zalando.nakadi.client.java.enumerator.EventEnrichmentStrategy;
import org.zalando.nakadi.client.java.enumerator.EventTypeCategory;
import org.zalando.nakadi.client.java.enumerator.PartitionStrategy;
import org.zalando.nakadi.client.java.model.EventType;
import org.zalando.nakadi.client.java.model.EventTypeSchema;
import org.zalando.nakadi.client.java.model.EventTypeStatistics;
import org.zalando.nakadi.client.java.model.Metrics;
import org.zalando.nakadi.client.java.test.event.generator.EventGenerator;
import org.zalando.nakadi.client.java.test.event.simple.MySimpleEventGenerator;
import org.zalando.nakadi.client.scala.ClientFactory;

public class ClientIntegrationTest {
    private Client client = ClientFactory.getJavaClient();

    @After
    public void shutdown() throws InterruptedException, ExecutionException {
        client.stop();
    }

    @Test
    public void createEventTypes() throws InterruptedException, ExecutionException {
        EventGenerator gen = new MySimpleEventGenerator()//
                .withEventTypeId("ClientIntegrationTest-Java-postGetDeleteEventTypes").build();
        EventType originalEventType = gen.getEventType();

        // POST
        client.createEventType(originalEventType).get();

        // GET
        Optional<EventType> eventTypeResult = client.getEventType(originalEventType.getName()).get();
        assertTrue("Created Event should be returned", eventTypeResult.isPresent());
        EventType eventType = eventTypeResult.get();

        assertEquals(eventType.getCategory(), originalEventType.getCategory());
        assertEquals(eventType.getDataKeyFields(), originalEventType.getDataKeyFields());
        assertEquals(eventType.getName(), originalEventType.getName());
        assertEquals(eventType.getOwningApplication(), originalEventType.getOwningApplication());
        assertEquals(eventType.getPartitionKeyFields(), originalEventType.getPartitionKeyFields());
        assertEquals(eventType.getPartitionStrategy(), originalEventType.getPartitionStrategy());
        assertEquals(eventType.getSchema().getSchema(), originalEventType.getSchema().getSchema());
        assertEquals(eventType.getSchema().getType(), originalEventType.getSchema().getType());
        assertEquals(eventType.getStatistics(), originalEventType.getStatistics());

    }

    @Test
    public void updateEventTypes() throws InterruptedException, ExecutionException {
        EventGenerator gen = new MySimpleEventGenerator()//
                .withEventTypeId("ClientIntegrationTest-Java-postGetDeleteEventTypes").build();
        EventType originalEventType = gen.getEventType();

        // POST
        client.createEventType(originalEventType).get();

        // GET
        Optional<EventType> eventTypeResult = client.getEventType(originalEventType.getName()).get();
        assertTrue("Created Event should be returned", eventTypeResult.isPresent());
        EventType eventType = eventTypeResult.get();

        assertEquals(eventType.getCategory(), originalEventType.getCategory());
        assertEquals(eventType.getDataKeyFields(), originalEventType.getDataKeyFields());
        assertEquals(eventType.getName(), originalEventType.getName());
        assertEquals(eventType.getOwningApplication(), originalEventType.getOwningApplication());
        assertEquals(eventType.getPartitionKeyFields(), originalEventType.getPartitionKeyFields());
        assertEquals(eventType.getPartitionStrategy(), originalEventType.getPartitionStrategy());
        assertEquals(eventType.getSchema().getSchema(), originalEventType.getSchema().getSchema());
        assertEquals(eventType.getSchema().getType(), originalEventType.getSchema().getType());
        assertEquals(eventType.getStatistics(), originalEventType.getStatistics());

        String name = eventType.getName();
        String owningApplication = "owningApplication";
        EventTypeCategory category = eventType.getCategory();
        List<EventEnrichmentStrategy> enrichmentStrategies = eventType.getEnrichmentStrategies();
        PartitionStrategy partitionStrategy = eventType.getPartitionStrategy();
        EventTypeSchema schema = eventType.getSchema();
        List<String> dataKeyFields = eventType.getDataKeyFields();
        List<String> partitionKeyFields = eventType.getPartitionKeyFields();
        EventTypeStatistics statistics = eventType.getStatistics();
        EventType changedEventType = new EventType(name, owningApplication, category, enrichmentStrategies,
                partitionStrategy, schema, dataKeyFields, partitionKeyFields, statistics);

        // Update
        client.updateEventType(originalEventType.getName(), changedEventType).get();
        Thread.sleep(3000);
        // GET
        eventTypeResult = client.getEventType(originalEventType.getName()).get();
        assertTrue("Created Event should NOT be returned", eventTypeResult.isPresent());
        assertEquals(eventTypeResult.get().getCategory(), originalEventType.getCategory());
        assertEquals(eventTypeResult.get().getDataKeyFields(), originalEventType.getDataKeyFields());
        assertEquals(eventTypeResult.get().getName(), originalEventType.getName());
        assertEquals(eventTypeResult.get().getOwningApplication(), changedEventType.getOwningApplication());
        assertEquals(eventTypeResult.get().getPartitionKeyFields(), originalEventType.getPartitionKeyFields());
        assertEquals(eventTypeResult.get().getPartitionStrategy(), originalEventType.getPartitionStrategy());
        assertEquals(eventTypeResult.get().getSchema().getSchema(), originalEventType.getSchema().getSchema());
        assertEquals(eventTypeResult.get().getSchema().getType(), originalEventType.getSchema().getType());
        assertEquals(eventTypeResult.get().getStatistics(), originalEventType.getStatistics());

    }

    @Test
    public void deleteEventTypes() throws InterruptedException, ExecutionException {
        EventGenerator gen = new MySimpleEventGenerator()//
                .withEventTypeId("ClientIntegrationTest-Java-postGetDeleteEventTypes").build();
        EventType originalEventType = gen.getEventType();

        // POST
        client.createEventType(originalEventType).get();

        // GET
        Optional<EventType> eventTypeResult = client.getEventType(originalEventType.getName()).get();
        assertTrue("Created Event should be returned", eventTypeResult.isPresent());
        EventType eventType = eventTypeResult.get();

        assertEquals(eventType.getCategory(), originalEventType.getCategory());
        assertEquals(eventType.getDataKeyFields(), originalEventType.getDataKeyFields());
        assertEquals(eventType.getName(), originalEventType.getName());
        assertEquals(eventType.getOwningApplication(), originalEventType.getOwningApplication());
        assertEquals(eventType.getPartitionKeyFields(), originalEventType.getPartitionKeyFields());
        assertEquals(eventType.getPartitionStrategy(), originalEventType.getPartitionStrategy());
        assertEquals(eventType.getSchema().getSchema(), originalEventType.getSchema().getSchema());
        assertEquals(eventType.getSchema().getType(), originalEventType.getSchema().getType());
        assertEquals(eventType.getStatistics(), originalEventType.getStatistics());

        // DELETE
        Void result = client.deleteEventType(originalEventType.getName()).get();

        // GET
        eventTypeResult = client.getEventType(originalEventType.getName()).get();
        assertFalse("Created Event should NOT be returned", eventTypeResult.isPresent());
    }

    @Test
    public void getMetrics() throws InterruptedException, ExecutionException {
        Optional<Metrics> result = client.getMetrics().get();
        assertTrue("Metrics should be returned", result.isPresent());
        Metrics metrics = result.get();
        assertNotNull("Version should be available", metrics.getVersion());
        assertTrue("Gauges should not be empty", metrics.getGauges().size() > 0);
    }

    @Test
    public void getEventTypes() throws InterruptedException, ExecutionException {
        Optional<List<EventType>> result = client.getEventTypes().get();
        List<EventType> events = result.get();
        assertTrue(result.isPresent());
        assertTrue(events.size() >= 0);
    }

    @Test
    public void getEnrichmentStrategies() throws InterruptedException, ExecutionException {
        Optional<List<EventEnrichmentStrategy>> result = client.getEnrichmentStrategies().get();
        assertTrue(result.isPresent());
        List<EventEnrichmentStrategy> enrichtmentStrategies = result.get();
        assertTrue("EventEnrichmentStrategy", enrichtmentStrategies.size() == 1);
    }

    @Test
    public void getPartitioningStrategies() throws InterruptedException, ExecutionException {
        Optional<List<PartitionStrategy>> result = client.getPartitioningStrategies().get();
        assertTrue(result.isPresent());
        List<PartitionStrategy> partitioningStrategies = result.get();
        assertTrue("PartitionStrategy", partitioningStrategies.size() == 3);
    }

}
