package org.zalando.nakadi.client;

import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;
import org.zalando.nakadi.client.utils.TestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JEventTest {
    private JListener.JEvent event;

    @Before
    public void setup() throws Exception {
        final TestUtils utils = new TestUtils();

        final Map<String, Object> meta = Maps.newHashMap();
        final HashMap<String, Object> metaTmp = Maps.newHashMap();
        metaTmp.put("b", "c");
        meta.put("a", utils.toScalaMap(metaTmp));

        final Map<String, Object> body = Maps.newHashMap();
        body.put("a", "v1");
        final HashMap<String, Object> bodyTmp = Maps.newHashMap();
        bodyTmp.put("b", "v2");
        body.put("c", utils.toScalaMap(bodyTmp));


        final Event actualEvent = new Event("eventType",
                                             "orderingKey",
                                             utils.toScalaMap(meta),
                                             utils.toScalaMap(body));

        event = new JListener.JEvent(actualEvent);
    }


    @Test
    public void testEventType() throws Exception {
        assertEquals("wrong event type", "eventType", event.getEventType());
    }


    @Test
    public void testOrderingKey() throws Exception {
        assertEquals("wrong ordering key", "orderingKey", event.getOrderingKey());
    }


    @Test
    public void testMeta() throws Exception {
        final Map<String, Object> meta = Maps.newHashMap();
        final HashMap<String, Object> metaTmp = Maps.newHashMap();
        metaTmp.put("b", "c");
        meta.put("a", metaTmp);

        assertEquals("wrong meta data", meta, event.getMetaData());
    }


    @Test
    public void testBody() throws Exception {
        final Map<String, Object> body = Maps.newHashMap();
        body.put("a", "v1");
        final HashMap<String, Object> bodyTmp = Maps.newHashMap();
        bodyTmp.put("b", "v2");
        body.put("c", bodyTmp);

        assertEquals("wrong body", body, event.getBody());
    }
}
