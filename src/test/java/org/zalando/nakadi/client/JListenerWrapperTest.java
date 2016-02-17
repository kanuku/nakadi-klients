package org.zalando.nakadi.client;


import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;
import org.zalando.nakadi.client.utils.TestUtils;
import scala.Some;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class JListenerWrapperTest {

    private JListenerWrapper wrapper;
    private MyListener listener;

    @Before
    public void setup() throws Exception {
        listener = new MyListener();
        wrapper = new JListenerWrapper(listener);
    }


    @Test
    public void testId() throws Exception {
        assertEquals(MyListener.ID, wrapper.id());
    }


    @Test
    public void testOnReceive() throws Exception {
        final TestUtils utils = new TestUtils();

        final String topic = "topic";
        final String partition = "partition";
        final Cursor cursor = new Cursor("partition", "offset");
        final Event event = new Event("eventType",
                                      "orderingKey",
                                      utils.toScalaMap(Maps.newHashMap()),
                                      utils.toScalaMap(Maps.newHashMap()));

        wrapper.onReceive(topic, partition, cursor, event);

        assertEquals("wrong topic", topic, listener.topic);
        assertEquals("wrong partition", partition, listener.partition);
        assertEquals("wrong cursor", cursor, listener.cursor);
        assertEquals(event.eventType(), listener.event.getEventType());
        assertEquals(event.orderingKey(), listener.event.getOrderingKey());
    }


    @Test
    public void testOnConnectionOpened() throws Exception {
        final String topic = "topic";
        final String partition = "partition";

        wrapper.onConnectionOpened(topic, partition);

        assertEquals("wrong topic", topic, listener.topic);
        assertEquals("wrong partition", partition, listener.partition);
    }


    @Test
    public void testOnConnectionFailed() throws Exception {
        final String topic = "topic";
        final String partition = "partition";
        final int status = 200;
        final String error = "error";

        wrapper.onConnectionFailed(topic, partition, status, error);

        assertEquals("wrong topic", topic, listener.topic);
        assertEquals("wrong partition", partition, listener.partition);
        assertEquals("wrong status", status, listener.status);
        assertEquals("wrong error", error, listener.error);
    }


    @Test
    public void testOnConnectionClosed() throws Exception {
        final String topic = "topic";
        final String partition = "partition";
        final Cursor cursor = new Cursor("partition", "offset");
        final Optional<Cursor> lastCursor = Optional.of(new Cursor("partition", "offset"));

        wrapper.onConnectionClosed(topic, partition, new Some<>(cursor));

        assertEquals("wrong topic", topic, listener.topic);
        assertEquals("wrong partition", partition, listener.partition);
        assertEquals("wrong cursor", lastCursor, listener.lastCursor);
    }


    private static final class MyListener implements JListener{

        String topic;
        String partition;
        Cursor cursor;
        JListener.JEvent event;
        int status;
        String error;
        Optional<Cursor> lastCursor;

        public static final String ID = "ID";

        @Override
        public String id() {
            return ID;
        }

        @Override
        public void onReceive(String topic, String partition, Cursor cursor, JEvent event) {
            this.topic = topic;
            this.partition = partition;
            this.cursor = cursor;
            this.event = event;
        }

        @Override
        public void onConnectionOpened(String topic, String partition) {
            this.topic = topic;
            this.partition = partition;
        }

        @Override
        public void onConnectionFailed(String topic, String partition, int status, String error) {
            this.topic = topic;
            this.partition = partition;
            this.status = status;
            this.error = error;
        }

        @Override
        public void onConnectionClosed(String topic, String partition, Optional<Cursor> lastCursor) {
            this.topic = topic;
            this.partition = partition;
            this.lastCursor = lastCursor;
        }
    }
}
