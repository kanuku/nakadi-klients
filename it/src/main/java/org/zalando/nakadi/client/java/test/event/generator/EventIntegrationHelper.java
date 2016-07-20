package org.zalando.nakadi.client.java.test.event.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.nakadi.client.java.Client;
import org.zalando.nakadi.client.java.enumerator.PartitionStrategy;
import org.zalando.nakadi.client.java.model.Event;
import org.zalando.nakadi.client.java.model.EventType;
import org.zalando.nakadi.client.java.model.Partition;

public class EventIntegrationHelper {

    private final EventGenerator gen;
    private final Client client;
    private final EventType eventType;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public EventIntegrationHelper(EventGenerator gen, Client client) {
        this.gen = gen;
        this.client = client;
        this.eventType = gen.getEventType();
    }

    public Optional<EventType> getEventType() {
        try {
            return client.getEventType(gen.getEventTypeName()).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(gen.getEventTypeId() + " - createEventType :" + e.getMessage(), e);
            return Optional.empty();
        }
    }

    public boolean createEventType() {
        try {
            client.createEventType(eventType).get();
            return true;
        } catch (RuntimeException | InterruptedException | ExecutionException e) {
            logError(gen.getEventTypeId() + " - createEventType :" + e.getMessage(), e);
            return false;
        }
    }

    @SuppressWarnings("static-access")
    private void logError(String identifier, Throwable t) {
        log.error("%s => %s ".format(identifier, t.getMessage()));
    }

    public EventGenerator getGen() {
        return gen;
    }

    public Client getClient() {
        return client;
    }

    public Integer getNumberOfPartitions() throws InterruptedException, ExecutionException {
        Optional<List<Partition>> result = client.getPartitions(eventType.getName()).get();
        return result.get().size();
    }
    public List<PartitionStrategy> getPartitionStrategies() throws InterruptedException, ExecutionException {
        return client.getPartitioningStrategies().get().get();
        
    }

    public List<Event> publishEvents(Integer nrOfEvents) throws InterruptedException, ExecutionException {
        List<Event> events = new ArrayList<Event>();
        IntStream.range(0, nrOfEvents).forEach(nr -> events.add(gen.getNewEvent()));
        client.publishEvents(eventType.getName(), events).get();
        log.info("Events published" + events.size());
        return events;

    }

}
