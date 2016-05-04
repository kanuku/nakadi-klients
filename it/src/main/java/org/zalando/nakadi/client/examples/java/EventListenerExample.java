package org.zalando.nakadi.client.examples.java;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import org.zalando.nakadi.client.Deserializer;
import org.zalando.nakadi.client.java.Client;
import org.zalando.nakadi.client.java.ClientError;
import org.zalando.nakadi.client.java.Listener;
import org.zalando.nakadi.client.java.model.Cursor;
import org.zalando.nakadi.client.java.model.Event;
import org.zalando.nakadi.client.java.model.EventStreamBatch;
import org.zalando.nakadi.client.scala.ClientFactory;
import org.zalando.nakadi.client.utils.ClientBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Optional;

public class EventListenerExample {

    

    /**
     * Implement the Listener interface
     */

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        EventListenerExample example = new EventListenerExample();
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
        Listener<MeetingsEvent> listener = new EventCounterListener("Java-Test");
        Cursor cursor = new Cursor("0", "BEGIN");

        String eventTypeName = "MeetingsEvent-example-E";
        TypeReference<EventStreamBatch<MeetingsEvent>> typeRef = new TypeReference<EventStreamBatch<MeetingsEvent>>() {
        };

        java.util.concurrent.Future<Void> result = client.subscribe(eventTypeName, java.util.Optional.of(cursor), listener, typeRef);

        result.get();

    }
}
