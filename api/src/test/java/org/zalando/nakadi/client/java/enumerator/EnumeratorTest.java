package org.zalando.nakadi.client.java.enumerator;


import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

@SuppressWarnings("static-access")
public class EnumeratorTest {

    @Test
    public void testBatchItemPublishingStatus() {
        for (BatchItemPublishingStatus e : BatchItemPublishingStatus.values()) {
            assertEquals("BatchItemPublishingStatus ", Optional.of(e), (e.withName(e.name())));
        }
    }

    @Test
    public void testBatchItemStep() {
        for (BatchItemStep e : BatchItemStep.values()) {
            assertEquals("BatchItemStep ", Optional.of(e), (e.withName(e.name())));
        }
    }

    @Test
    public void testDataOperation() {
        for (DataOperation e : DataOperation.values()) {
            assertEquals("DataOperation ", Optional.of(e), (e.withName(e.name())));
        }
    }

    @Test
    public void testEventEnrichmentStrategy() {
        for (EventEnrichmentStrategy e : EventEnrichmentStrategy.values()) {
            assertEquals("EventEnrichmentStrategy ", Optional.of(e), (e.withName(e.name())));
        }
    }

    @Test
    public void testEventTypeCategory() {
        for (EventTypeCategory e : EventTypeCategory.values()) {
            assertEquals("EventTypeCategory ", Optional.of(e), (e.withName(e.name())));
        }
    }

    @Test
    public void testPartitionStrategy() {
        for (PartitionStrategy e : PartitionStrategy.values()) {
            assertEquals("PartitionStrategy ", Optional.of(e), (e.withName(e.name())));
        }
    }

    @Test
    public void testSchemaType() {
        for (SchemaType e : SchemaType.values()) {
            assertEquals("SchemaType ", Optional.of(e), (e.withName(e.name())));
        }
    }
    @Test
    public void testCompatibilityMode() {
    	for (CompatibilityMode e : CompatibilityMode.values()) {
    		assertEquals("CompatibilityMode ", Optional.of(e), (e.withName(e.name())));
    	}
    }

}
