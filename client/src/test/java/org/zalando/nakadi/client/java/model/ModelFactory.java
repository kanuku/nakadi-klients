package org.zalando.nakadi.client.java.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.zalando.nakadi.client.java.ClientError;
import org.zalando.nakadi.client.java.enumerator.BatchItemPublishingStatus;
import org.zalando.nakadi.client.java.enumerator.BatchItemStep;
import org.zalando.nakadi.client.java.enumerator.DataOperation;
import org.zalando.nakadi.client.java.enumerator.EventEnrichmentStrategy;
import org.zalando.nakadi.client.java.enumerator.EventTypeCategory;
import org.zalando.nakadi.client.java.enumerator.PartitionStrategy;
import org.zalando.nakadi.client.java.enumerator.SchemaType;
import org.zalando.nakadi.client.java.model.BatchItemResponse;
import org.zalando.nakadi.client.java.model.BusinessEvent;
import org.zalando.nakadi.client.java.model.Cursor;
import org.zalando.nakadi.client.java.model.DataChangeEvent;
import org.zalando.nakadi.client.java.model.Event;
import org.zalando.nakadi.client.java.model.EventMetadata;
import org.zalando.nakadi.client.java.model.EventStreamBatch;
import org.zalando.nakadi.client.java.model.EventType;
import org.zalando.nakadi.client.java.model.EventTypeSchema;
import org.zalando.nakadi.client.java.model.EventTypeStatistics;
import org.zalando.nakadi.client.java.model.Metrics;
import org.zalando.nakadi.client.java.model.Partition;
import org.zalando.nakadi.client.java.model.Problem;

import com.google.common.collect.Lists;

public final class ModelFactory {

    private static Random randomizer = new Random();

    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    public static BatchItemResponse newBatchItemResponse() {
        String detail = "detail";
        BatchItemStep step = BatchItemStep.ENRICHING;
        BatchItemPublishingStatus publishingStatus = BatchItemPublishingStatus.SUBMITTED;
        String eid = randomUUID();
        return new BatchItemResponse(eid, publishingStatus, step, detail);
    }

    public static BusinessEvent newBusinessEvent() {
        return new BusinessEventImpl(newEventMetadata());
    }

    public static EventMetadata newEventMetadata() {
        return new EventMetadata(randomUUID(), //
                "eventType", "occurredAt", //
                "receivedAt", Lists.newArrayList("parentEids"), "flowId", "partition");
    }

    public static Cursor newCursor() {
        return new Cursor(randomUUID(), randomUUID());
    }

    public static SimpleEvent newSimpleEvent() {
        return new SimpleEvent(randomUUID());
    }

    public static <T extends Event> DataChangeEvent<T> newDataChangeEvent(T event) {
        return new DataChangeEvent<T>(event, randomUUID(), DataOperation.CREATE, newEventMetadata());
    }

    public static <T extends Event> EventStreamBatch<T> newEventStreamBatch(List<T> events, Cursor cursor) {
        return new EventStreamBatch<T>(cursor, events);
    }

    public static EventTypeSchema newEventTypeSchema() {
        String schema = "{'propeties':{'key':'value'}}".replaceAll("'", "\"");
        return new EventTypeSchema(SchemaType.JSON, schema);
    }

    public static Integer randomInt() {
        return randomizer.nextInt();
    }

    public static EventTypeStatistics newEventTypeStatistics() {
        return new EventTypeStatistics(randomInt(), randomInt(), randomInt(), randomInt());
    }

    public static Metrics newMetrics() {
        int depth = 5;
        Map<String, Object> gauges = new HashMap<String, Object>();
        gauges.put("normalString", randomUUID());
        gauges.put("SimpleEvent", newSimpleEvent());
        gauges.put("List", randomListOfString(depth));
        gauges.put("List", randomListOfString(depth));
        gauges.put("MapOfMaps", randomMapOfString(depth));
        return new Metrics("version", gauges);
    }

    public static List<String> randomListOfString(int nrOfItems) {
        return Stream.generate(() -> randomUUID()).limit(nrOfItems).collect(Collectors.toList());

    }

    /**
     * A function that call itself to generate maps that contain other maps as values.
     * 
     * @param nrOfItems
     * @return
     */
    public static Map<String, Object> randomMapOfString(int nrOfItems) {
        return Stream.generate(() -> randomUUID()).limit(nrOfItems).collect(Collectors.toMap(i -> "nrOfItems-" + nrOfItems + "--" + i, i -> randomMapOfString(nrOfItems - 1)));
    }

    public static Partition newPartition() {

        return new Partition(randomUUID(), randomUUID(), randomUUID());
    }

    public static Problem newProblem() {

        return new Problem(randomUUID(), randomUUID(), randomInt(), randomUUID(), randomUUID());
    }

    public static EventType newEventType() {
        List<EventEnrichmentStrategy> newEnrichmentStrategy = Lists.newArrayList(EventEnrichmentStrategy.METADATA);
        return new EventType(randomUUID(), randomUUID(), EventTypeCategory.BUSINESS, //
                newEnrichmentStrategy, newPartitionStrategy(), newEventTypeSchema(), randomListOfString(12), randomListOfString(21), newEventTypeStatistics());
    }

    public static PartitionStrategy newPartitionStrategy() {
        return PartitionStrategy.values()[randomizer.nextInt(PartitionStrategy.values().length)];
    }

    public static EventEnrichmentStrategy newEventEnrichmentStrategy() {

        return EventEnrichmentStrategy.values()[randomizer.nextInt(EventEnrichmentStrategy.values().length)];
    }

    public static EventTypeCategory newEventTypeCategory() {
        return EventTypeCategory.values()[randomizer.nextInt(EventTypeCategory.values().length)];
    }

    public static ClientError newClientError() {
        return new ClientError(randomUUID(), Optional.of(randomInt()), Optional.of(new IllegalStateException()));
    }

}
