package org.zalando.client.java;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Test;
import org.zalando.nakadi.client.java.Client;
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
}
