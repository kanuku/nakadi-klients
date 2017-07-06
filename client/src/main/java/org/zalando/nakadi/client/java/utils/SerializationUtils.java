package org.zalando.nakadi.client.java.utils;

import java.util.List;

import org.zalando.nakadi.client.*;
import org.zalando.nakadi.client.java.enumerator.*;
import org.zalando.nakadi.client.java.model.*;

import com.fasterxml.jackson.core.type.TypeReference;

public class SerializationUtils {

    public static <T> Serializer<T> defaultSerializer() {

        return JavaJacksonJsonMarshaller.serializer();

    }
    
    public static <T> Serializer<List<T>> defaultListSerializer() {
        
        return JavaJacksonJsonMarshaller.serializer();
        
    }
    
    public static <T> Deserializer<T> withCustomDeserializer(TypeReference<T> in){
        return JavaJacksonJsonMarshaller.deserializer(in);
    }

    public static Deserializer<Metrics> metricsDeserializer() {
        return JavaJacksonJsonMarshaller.deserializer(JavaJacksonJsonMarshaller.metricsTR());
    }

    public static Deserializer<EventType> eventTypeDeserializer() {
        return JavaJacksonJsonMarshaller.deserializer(JavaJacksonJsonMarshaller.eventTypeTR());
    }

    public static Deserializer<Partition> partitionDeserializer() {
        return JavaJacksonJsonMarshaller.deserializer(JavaJacksonJsonMarshaller.partitionTR());
    }

    public static Deserializer<List<EventType>> seqOfEventTypeDeserializer() {
        return JavaJacksonJsonMarshaller.deserializer(JavaJacksonJsonMarshaller.listOfEventTypeTR());
    }

    public static Deserializer<List<Partition>> seqOfPartitionDeserializer() {
        return JavaJacksonJsonMarshaller.deserializer(JavaJacksonJsonMarshaller.listOfPartitionTR());
    }

    public static Deserializer<List<EventEnrichmentStrategy>> seqOfEventEnrichmentStrategy() {
        return JavaJacksonJsonMarshaller.deserializer(JavaJacksonJsonMarshaller.listOfEventEnrichmentStrategyTR());
    }

    public static Deserializer<List<PartitionStrategy>> seqOfPartitionStrategy() {
        return JavaJacksonJsonMarshaller.deserializer(JavaJacksonJsonMarshaller.listOfPartitionStrategyTR());
    }
}
