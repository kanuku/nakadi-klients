package org.zalando.nakadi.client.java.utils;

import java.util.List;

import org.zalando.nakadi.client.Deserializer;
import org.zalando.nakadi.client.Serializer;
import org.zalando.nakadi.client.java.enumerator.*;
import org.zalando.nakadi.client.java.model.*;

public class SerializationUtils {

    public <T extends Event> Serializer<T> defaultSerializer() {

        return JavaJacksonJsonMarshaller.serializer();

    }

    public Deserializer<Metrics> metricsDeserializer() {
        return JavaJacksonJsonMarshaller.deserializer(JavaJacksonJsonMarshaller.metricsTR());
    }

    public Deserializer<EventType> eventTypeDeserializer() {
        return JavaJacksonJsonMarshaller.deserializer(JavaJacksonJsonMarshaller.eventTypeTR());
    }

    // public Deserializer<T> customDeserializer(TypeReference[T] in) {
    // return JavaJacksonJsonMarshaller.deserializer(tr);
    // }

    public Deserializer<Partition> partitionDeserializer() {
        return JavaJacksonJsonMarshaller.deserializer(JavaJacksonJsonMarshaller.partitionTR());
    }

    public Deserializer<List<EventType>> seqOfEventTypeDeserializer() {
        return JavaJacksonJsonMarshaller.deserializer(JavaJacksonJsonMarshaller.listOfEventTypeTR());
    }

    public Deserializer<List<Partition>> seqOfPartitionDeserializer() {
        return JavaJacksonJsonMarshaller.deserializer(JavaJacksonJsonMarshaller.listOfPartitionTR());
    }

    public Deserializer<List<EventValidationStrategy>> seqOfEventValidationStrategy() {
        return JavaJacksonJsonMarshaller.deserializer(JavaJacksonJsonMarshaller.listOfEventValidationStrategyTR());
    }

    public Deserializer<List<EventEnrichmentStrategy>> seqOfEventEnrichmentStrategy() {
        return JavaJacksonJsonMarshaller.deserializer(JavaJacksonJsonMarshaller.listOfEventEnrichmentStrategyTR());
    }

    public Deserializer<List<PartitionStrategy>> seqOfPartitionStrategy() {
        return JavaJacksonJsonMarshaller.deserializer(JavaJacksonJsonMarshaller.listOfPartitionStrategyTR());
    }
}
