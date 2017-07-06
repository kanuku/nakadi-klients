package org.zalando.client.java;

import static org.junit.Assert.*;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.junit.*;
import org.zalando.nakadi.client.java.Client;
import org.zalando.nakadi.client.java.model.EventType;
import org.zalando.nakadi.client.java.test.event.dce.payment.PaymentEventGenerator;
import org.zalando.nakadi.client.java.test.event.generator.*;
import org.zalando.nakadi.client.scala.ClientFactory;

public class PaymentCreatedDataChangeEventTest {

    private Client client = ClientFactory.buildJavaClient();

//    private TypeReference<EventStreamBatch<DataChangeEvent<PaymentCreated>>> typeRef = new TypeReference<EventStreamBatch<DataChangeEvent<PaymentCreated>>>() {
//    };

    @After
    public void shutdown() throws InterruptedException, ExecutionException {
        client.stop();
    }

    @Test
    public void createSchema() throws InterruptedException, ExecutionException {

        EventGenerator gen = new PaymentEventGenerator()//
                .withEventTypeId("PaymentCreatedDataChangeEventTest-createSchema")//
                .build();
        EventIntegrationHelper it = new EventIntegrationHelper(gen, client);
        assertTrue(it.createEventType());
        assertTrue(it.getEventType().isPresent());
        Optional<EventType> result = client.getEventType("none-existing-event-type-name").get();
        assertEquals(result.isPresent(), false);
    } 
    @Test
    public void createEventType() throws InterruptedException, ExecutionException {
        
        EventGenerator gen = new PaymentEventGenerator()//
        .withEventTypeId("PaymentCreatedDataChangeEventTest-createEventType")//
        .build();
        EventIntegrationHelper it = new EventIntegrationHelper(gen, client);
        assertTrue(it.createEventType());
        assertTrue(it.getEventType().isPresent());
        
        it.publishEvents(12);
    } 

}
