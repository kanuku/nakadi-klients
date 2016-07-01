package org.zalando.client.java;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Test;
import org.zalando.nakadi.client.java.Client;
import org.zalando.nakadi.client.java.enumerator.EventEnrichmentStrategy;
import org.zalando.nakadi.client.java.enumerator.PartitionStrategy;
import org.zalando.nakadi.client.java.model.EventType;
import org.zalando.nakadi.client.java.model.Metrics;
import org.zalando.nakadi.client.scala.ClientFactory;

public class ClientIntegrationTest {
    private Client client = ClientFactory.getJavaClient();

    @After
    public void shutdown() throws InterruptedException, ExecutionException {
        client.stop();
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
    public void getEventTypes() throws InterruptedException, ExecutionException{
        Optional<List<EventType>> result = client.getEventTypes().get();
        List<EventType> events = result.get();
        assertTrue(result.isPresent());
        assertTrue(events.size()>=0);
        
    }
    
    @Test
    public void getEnrichmentStrategies() throws InterruptedException, ExecutionException{
        Optional<List<EventEnrichmentStrategy>> result = client.getEnrichmentStrategies().get();
        assertTrue(result.isPresent());
        List<EventEnrichmentStrategy> enrichtmentStrategies = result.get();
        assertTrue("EventEnrichmentStrategy",enrichtmentStrategies.size() == 1);
    }
    @Test
    public void getPartitioningStrategies() throws InterruptedException, ExecutionException{
        Optional<List<PartitionStrategy>> result = client.getPartitioningStrategies().get();
        assertTrue(result.isPresent());
        List<PartitionStrategy> partitioningStrategies = result.get();
        assertTrue("PartitionStrategy",partitioningStrategies.size() ==3);
    }
    
   
        
}
