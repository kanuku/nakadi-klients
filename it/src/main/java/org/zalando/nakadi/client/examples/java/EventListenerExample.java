package org.zalando.nakadi.client.examples.java;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.zalando.nakadi.client.java.Client;
import org.zalando.nakadi.client.java.ClientError;
import org.zalando.nakadi.client.java.model.Cursor;
import org.zalando.nakadi.client.java.model.Event;
import org.zalando.nakadi.client.scala.ClientFactory;
import org.zalando.nakadi.client.utils.ClientBuilder;
import org.zalando.nakadi.client.java.Listener;

public class EventListenerExample {

    /**
     * Define how your event should look like
     */
    public class MeetingsEvent implements Event {
        private final String date;
        private final String topic;

        public MeetingsEvent(String date, String topic) {
            this.date = date;
            this.topic = topic;
        }

        public String getDate() {
            return date;
        }

        public String getTopic() {
            return topic;
        }

    }

    /**
     * Implement the Listener interface
     */
    class EventCounterListener implements org.zalando.nakadi.client.java.Listener<MeetingsEvent> {
        private final String id;
        private AtomicLong eventCount = new AtomicLong(0);

        public EventCounterListener(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public void onReceive(String eventUrl, Cursor cursor, List<MeetingsEvent> events) {
            eventCount.addAndGet(events.size());
            System.out.println("#####################################");
            System.out.println("Received " + events.size());
            System.out.println(String.format("Has a total of %d events",eventCount.get()) );
            System.out.println("#####################################");

        }

        @Override
        public void onError(String eventUrl, Cursor cursor, ClientError error) {
           System.err.println(String.format("Error %s %s %s",eventUrl, cursor, error));

        }

    }

    public static void main(String[] args) {
        EventListenerExample example= new EventListenerExample();
        /**
         * Create client
         */
        final Client client = new ClientBuilder()//
                        .withHost(ClientFactory.host())//
                        .withSecuredConnection(true) // s
                        .withVerifiedSslCertificate(false) // s
                        .withTokenProvider4Java(() -> ClientFactory.getToken())//
                        .buildJavaClient();
        
        
        
        /**
         * Initialize our Listener
         */
//        Listener<MeetingsEvent> listener = example.new EventCounterListener<MeetingsEvent>("Test");
        
        
        
        
        
    }
}
