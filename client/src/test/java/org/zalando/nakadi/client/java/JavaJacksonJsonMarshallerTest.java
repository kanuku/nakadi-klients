package org.zalando.nakadi.client.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.nakadi.client.java.model.BatchItemResponse;
import org.zalando.nakadi.client.java.model.BusinessEvent;
import org.zalando.nakadi.client.java.model.Cursor;
import org.zalando.nakadi.client.java.model.DataChangeEvent;
import org.zalando.nakadi.client.java.model.DataChangeEventQualifier;
import org.zalando.nakadi.client.java.model.Event;
import org.zalando.nakadi.client.java.model.EventMetadata;
import org.zalando.nakadi.client.java.model.EventStreamBatch;
import org.zalando.nakadi.client.java.model.EventTypeSchema;
import org.zalando.nakadi.client.java.model.EventTypeStatistics;
import org.zalando.nakadi.client.java.model.JavaJacksonJsonMarshaller;
import org.zalando.nakadi.client.java.model.Metrics;
import org.zalando.nakadi.client.java.model.Partition;
import org.zalando.nakadi.client.java.model.Problem;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;

public class JavaJacksonJsonMarshallerTest {
    Logger log = LoggerFactory.getLogger(this.getClass());

    private <T> String toJson(T in) throws JsonProcessingException {
        log.info("ToJson - in {}", in.toString());
        String result = JavaJacksonJsonMarshaller.serializer().to(in);
        log.info("ToJson - out {}", result);
        return result;
    }

    private <T> T toObject(String json, TypeReference<T> expectedType) throws JsonProcessingException {
        log.info("toObject - in {}", json);
        T result = JavaJacksonJsonMarshaller.deserializer(expectedType).from(json);
        log.info("toObject - out {}", result.toString());

        return result;
    }

    @Test
    public void testBatchItemResponse() throws JsonProcessingException {
        // Create object
        BatchItemResponse in = ModelFactory.newBatchItemResponse();
        String json = toJson(in);
        BatchItemResponse out = toObject(json, JavaJacksonJsonMarshaller.batchItemResponseTR());

        // Check
        compareBatchItemResponse(in, out);
        assertEquals(json,toJson(out));
    }

    @Test
    public void testBusinessEvent() throws JsonProcessingException {
        BusinessEvent in = ModelFactory.newBusinessEvent();
        String json = toJson(in);
        BusinessEvent out = toObject(json, new TypeReference<BusinessEventImpl>() {
        });
        compareMetadata(in.metadata(), out.metadata());
        assertEquals(json,toJson(out));
    }

    @Test
    public void testCursor() throws JsonProcessingException {
        Cursor in = ModelFactory.newCursor();
        String json = toJson(in);
        Cursor out = toObject(json, JavaJacksonJsonMarshaller.cursorTR());
        compareCursor(in, out);
        assertEquals(json,toJson(out));
    }

    @Test
    public void testDataChangeEvent() throws JsonProcessingException {
        SimpleEvent event = ModelFactory.newSimpleEvent();
        DataChangeEvent<SimpleEvent> in = ModelFactory.newDataChangeEvent(event);
        String json = toJson(in);
        DataChangeEvent<SimpleEvent> out = toObject(json, new TypeReference<DataChangeEvent<SimpleEvent>>() {
        });

        compareDataChangeEvent(in, out);
        compareSimpleEvent(in.getData(), out.getData());
        assertEquals(json,toJson(out));

    }

    @Test
    public void testEventMetadata() throws JsonProcessingException {
        EventMetadata in = ModelFactory.newEventMetadata();
        String json = toJson(in);
        EventMetadata out = toObject(json, JavaJacksonJsonMarshaller.eventMetadataTR());
        compareMetadata(in, out);
        assertEquals(json,toJson(out));
    }

    @Test
    public void testEventStreamBatch() throws JsonProcessingException {
        Cursor cursor = ModelFactory.newCursor();
        List<SimpleEvent> events = Lists.newArrayList(ModelFactory.newSimpleEvent(), ModelFactory.newSimpleEvent());
        EventStreamBatch<SimpleEvent> in = ModelFactory.newEventStreamBatch(events, cursor);
        TypeReference<EventStreamBatch<SimpleEvent>> expectedType = new TypeReference<EventStreamBatch<SimpleEvent>>() {
        };
        String json = toJson(in);
        EventStreamBatch<SimpleEvent> out = toObject(json, expectedType);
        compareEventStreamBatch(in, out);
        compareSimpleEvents(in.getEvents(), out.getEvents());
        assertEquals(json,toJson(out));
    }

    @Test
    public void testEventTypeSchema() throws JsonProcessingException {
        EventTypeSchema in = ModelFactory.newEventTypeSchema();
        String json = toJson(in);
        EventTypeSchema out = toObject(json, JavaJacksonJsonMarshaller.eventTypeSchemaTR());
        compareEventTypeSchema(in, out);
        assertEquals(json,toJson(out));

    }

    @Test
    public void testEventTypeStatistics() throws JsonProcessingException {
        EventTypeStatistics in = ModelFactory.newEventTypeStatistics();
        String json = toJson(in);
        EventTypeStatistics out = toObject(json, JavaJacksonJsonMarshaller.eventTypeStatisticsTR());
        compareEventTypeStatistics(in, out);
        assertEquals(json,toJson(out));
    }

    @Test
    public void testMetrics() throws JsonProcessingException {
        Metrics in = ModelFactory.newMetrics();
        String json = toJson(in);
        Metrics out = toObject(json, JavaJacksonJsonMarshaller.metricsTR());
        assertEquals(json,toJson(out));
    }

    @Test
    public void testPartition() throws JsonProcessingException {
        Partition in = ModelFactory.newPartition();
        String json = toJson(in);
        Partition out = toObject(json, JavaJacksonJsonMarshaller.partitionTR());
        comparePartition(in,out);
        assertEquals(json,toJson(out));
    }

  

    @Test
    public void testProblem() throws JsonProcessingException {
        Problem in = ModelFactory.newProblem();
        String json = toJson(in);
        Problem out = toObject(json, JavaJacksonJsonMarshaller.problemTR());
        compareProblem(in,out);
        assertEquals(json,toJson(out));
        
    }
    
    private void compareProblem(Problem left, Problem right) {
        assertEquals(left.getDetail(), right.getDetail());
        assertEquals(left.getInstance(), right.getInstance());
        assertEquals(left.getStatus(), right.getStatus());
        assertEquals(left.getTitle(), right.getTitle());
        assertEquals(left.getType(), right.getType());
        assertTrue(left.equals(right));
        
    }

    private void comparePartition(Partition left, Partition right) {
        assertEquals(left.getNewestAvailableOffset(), right.getNewestAvailableOffset());
        assertEquals(left.getOldestAvailableOffset(), right.getOldestAvailableOffset());
        assertEquals(left.getPartition(), right.getPartition());
        assertTrue(left.equals(right));
         
     }

    private void compareEventTypeSchema(EventTypeSchema left, EventTypeSchema right) {
        assertEquals(left.getType(), right.getType());
        assertEquals(left.getSchema(), right.getSchema());
        assertTrue(left.equals(right));
    }

    private void compareBatchItemResponse(BatchItemResponse left, BatchItemResponse right) {
        assertEquals(left.getDetail(), right.getDetail());
        assertEquals(left.getEid(), right.getEid());
        assertEquals(left.getPublishingStatus(), right.getPublishingStatus());
        assertEquals(left.getStep(), right.getStep());
        assertEquals(left.toString(), right.toString());
        assertTrue(left.equals(right));
    }

    private void compareMetadata(EventMetadata left, EventMetadata right) {
        assertEquals(left.getEid(), right.getEid());
        assertEquals(left.getEventTypeName(), right.getEventTypeName());
        assertEquals(left.getFlowId(), right.getFlowId());
        assertEquals(left.getOccurredAt(), right.getOccurredAt());
        assertEquals(left.getParentEids(), right.getParentEids());
        assertEquals(left.getPartition(), right.getPartition());
        assertEquals(left.getReceivedAt(), right.getReceivedAt());
        assertEquals(left.toString(), right.toString());
        assertTrue(left.equals(right));
        assertTrue(left.equals(right));
    }

    private void compareEventTypeStatistics(EventTypeStatistics left, EventTypeStatistics right) {
        assertEquals(left.getMessageSize(), right.getMessageSize());
        assertEquals(left.getMessagesPerMinute(), right.getMessagesPerMinute());
        assertEquals(left.getReadParallelism(), right.getReadParallelism());
        assertEquals(left.getWriteParallelism(), right.getWriteParallelism());
        assertTrue(left.equals(right));

    }

    

    private void compareCursor(Cursor left, Cursor right) {
        assertEquals(left.getOffset(), right.getOffset());
        assertEquals(left.getPartition(), right.getPartition());
        assertTrue(left.equals(right));
    }

    private void compareDataChangeEvent(DataChangeEvent<SimpleEvent> left, DataChangeEvent<SimpleEvent> right) {
        assertEquals(left.getData(), right.getData());
        compareDataChangeEventQualifier((DataChangeEventQualifier) left, (DataChangeEventQualifier) right);
        compareMetadata(left.getMetadata(), right.getMetadata());
        assertTrue(left.equals(right));
    }

    private void compareDataChangeEventQualifier(DataChangeEventQualifier left, DataChangeEventQualifier right) {
        assertEquals(left.getDataOperation(), right.getDataOperation());
        assertEquals(left.getDataType(), right.getDataType());
        assertTrue(left.equals(right));
    }

    private <T extends Event> void compareEventStreamBatch(EventStreamBatch<T> left, EventStreamBatch<T> right) {
        compareCursor(left.getCursor(), right.getCursor());
        assertTrue(left.equals(right));

    }

    private void compareSimpleEvent(SimpleEvent left, SimpleEvent right) {
        assertEquals(left.getId(), left.getId());
        assertTrue(left.equals(right));
    }

    private void compareSimpleEvents(List<SimpleEvent> left, List<SimpleEvent> right) {
        for (SimpleEvent simpleEvent : left) {
            Stream<SimpleEvent> rightStream = right.stream();
            Predicate<SimpleEvent> predicate = sameEventPredicate(simpleEvent);
            Stream<SimpleEvent> result = rightStream.filter(predicate);
            assertEquals("Should find exact one single", result.count(), 1);
        }
    }

    private Predicate<SimpleEvent> sameEventPredicate(SimpleEvent left) {
        return new Predicate<SimpleEvent>() {

            @Override
            public boolean test(SimpleEvent right) {
                return left.equals(right);
            }

        };
    }

}
