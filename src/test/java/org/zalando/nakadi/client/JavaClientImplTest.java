package org.zalando.nakadi.client;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.zalando.nakadi.client.utils.KlientMock;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JavaClientImplTest {

    private Client client;

    @Before
    public void setup() throws Exception {
        client = new JavaClientImpl(new KlientMock(KlientMock.defaultMockReturnValues()));
    }


    @Test
    public void testGetMetrics() throws Exception {
        final Future<Map<String,Object>> f = client.getMetrics();
        final Map<String,Object> a = f.get();
        assertTrue("missing key", a.containsKey("a"));

        final Map<String,Object> b = (Map<String,Object>) a.get("a");
        assertTrue("missing key in value map", b.containsKey("b"));

        final Map<String,Object> same = f.get(1L, TimeUnit.SECONDS);
        Assert.assertEquals("metrics maps are not equal", a, same);
    }

    @Test
    public void testGetPartition() throws Exception {
        final Future<TopicPartition> f = client.getPartition("topic", "partitionId");
        final TopicPartition partition = f.get();
        assertEquals("partitionid", partition.partitionId());
        assertEquals("oldestAvailableOffset", partition.oldestAvailableOffset());
        assertEquals("newestAvailableOffset", partition.newestAvailableOffset());
        assertEquals("partitions are not equal", partition, f.get(1L, TimeUnit.SECONDS));
    }

    @Test
    public void testGetPartitions() throws Exception {
        final Future<List<TopicPartition>> partitionsFuture = client.getPartitions("topic");
        final List<TopicPartition> partitions = partitionsFuture.get();
        assertEquals("illegal partition list size", 1, partitions.size());
        final TopicPartition p = partitions.get(0);
        assertEquals("partitionid", p.partitionId());
        assertEquals("oldestAvailableOffset", p.oldestAvailableOffset());
        assertEquals("newestAvailableOffset", p.newestAvailableOffset());
        assertEquals("partition lists are not equal", partitions, partitionsFuture.get(1L, TimeUnit.SECONDS));
    }

    @Test
    public void testGetTopics() throws Exception {
        final Future<List<Topic>> f = client.getTopics();
        final List<Topic> topics = f.get();
        assertEquals("illegal topic list size", 1, topics.size());
        final Topic topic = topics.get(0);
        assertEquals("wrong topic name", "topic",topic.name());
        assertEquals("topic lists are not equal", topics, f.get(1L, TimeUnit.SECONDS));
    }
}
