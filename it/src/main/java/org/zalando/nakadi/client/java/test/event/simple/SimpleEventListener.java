package org.zalando.nakadi.client.java.test.event.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.nakadi.client.java.ClientError;
import org.zalando.nakadi.client.java.Listener;
import org.zalando.nakadi.client.java.model.Cursor;

public class SimpleEventListener implements Listener<MySimpleEvent> {
    private List<MySimpleEvent> receivedEvents = new ArrayList<MySimpleEvent>();
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public SimpleEventListener(){
        
    }
    
    @Override
    public String getId() {
        return "MySimpleEventListner"; 
    }

    @Override
    public void onReceive(String endpoint, Cursor cursor, List<MySimpleEvent> events) {
        receivedEvents.addAll(events);
        log.info("Received: {}", events.size());
        log.info("Total: {}", receivedEvents.size());

    }

    @Override
    public void onSubscribed(String endpoint, Optional<Cursor> cursor) {
        log.info("Subscribed to Endpoint {} - cursor {}", endpoint, cursor);

    }

    @Override
    public void onError(String eventType, Optional<ClientError> error) {
        log.error("Error {}", error);

    }

    
    public List<MySimpleEvent> getReceivedEvents() {
        return receivedEvents;
    }

    public List<MySimpleEvent> waitToReceive(Integer nrOfEvents) throws InterruptedException {
        while (nrOfEvents > receivedEvents.size()){
            log.info("Total: {}", receivedEvents.size());
            log.info("Number of events to wait "+nrOfEvents);
            Thread.sleep(2000);
        }
        log.info("##############");
        log.info("Waited to receive {} events, actual size ={}", nrOfEvents, receivedEvents.size());
        log.info("##############");
        return receivedEvents;
    }
    
    
}
