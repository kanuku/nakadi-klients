package de.zalando.nakadi.client;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.zalando.nakadi.client.domain.*;
import de.zalando.nakadi.client.utils.NakadiTestService;
import de.zalando.nakadi.client.utils.Request;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class NakadiClientImplTest {

    private NakadiTestService service;
    private Client client;
    private final ObjectMapper objectMapper;

    private static final String TOKEN = "<OAUTH Token>";
    private static final String HOST = "localhost";
    private static final int PORT = 8081;
    private static final String MEDIA_TYPE = "application/json";


    public NakadiClientImplTest() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
    }

    @Before
    public void setup() throws Exception {

        final ClientBuilder builder = new ClientBuilder();
        final URI nakadiHost = new URI(String.format("http://%s:%s", HOST, PORT));
        client = builder.withOAuth2TokenProvider(() -> TOKEN)
                        .withEndpoint(nakadiHost)
                        .build();

    }

    @After
    public void tearDown() throws Exception {
        if (service != null) {
            service.stop();
        }
    }


    private Request performStandardRequestChecks(final String expectedRequestPath, final HttpString expectedRequestMethod){

        final Map<String, Collection<Request>> collectedRequestsMap = service.getCollectedRequests();
        assertEquals("unexpected number of requests", 1, collectedRequestsMap.size());

        final Collection<Request> requests = collectedRequestsMap.get(expectedRequestPath);
        assertNotNull("request " + expectedRequestPath + " was not recorded", requests);

        final Request request = Iterators.getLast(requests.iterator());

        assertEquals("invalid request path. test must be buggy", expectedRequestPath, request.getRequestPath());
        assertEquals("invalid request method used by request", expectedRequestMethod, request.getRequestMethod());

        final HeaderMap headerMap = request.getRequestHeaders();
        HeaderValues headerValues = headerMap.get(Headers.CONTENT_TYPE);
        final String mediaType = headerValues.getFirst();
        assertEquals("invalid media type in request", mediaType, MEDIA_TYPE);

        headerValues = headerMap.get(Headers.AUTHORIZATION);
        final String authorizationHeaderValue = headerValues.getFirst();
        assertEquals("Authorization header in request is not set or invalid","Bearer " + TOKEN, authorizationHeaderValue);

        return request;
    }

    @Test
    public void testGetMetrics() throws Exception {

        final HashMap<String, Object> expectedResponse = Maps.newHashMap();

        final HashMap<String, Object> getMetricsData = Maps.newHashMap();
        getMetricsData.put("calls_per_second", "0.011");
        getMetricsData.put("count", "1");
        Map<String, Object> statusCodeMetrics = Maps.newHashMap();
        statusCodeMetrics.put("401", "1");
        getMetricsData.put("status_codes",statusCodeMetrics);
        expectedResponse.put("get_metrics", getMetricsData);

        final HashMap<String, Object> postEventData = Maps.newHashMap();
        postEventData.put("calls_per_second", "0.005");
        postEventData.put("count", "5");
        statusCodeMetrics = Maps.newHashMap();
        statusCodeMetrics.put("201", "5");
        postEventData.put("status_codes", statusCodeMetrics);
        expectedResponse.put("post_event", postEventData);

        // ---

        final String expectedResponseAsString = objectMapper.writeValueAsString(expectedResponse);
        final HttpString requestMethod = new HttpString("GET");
        final String requestPath = "/metrics";
        final int responseStatusCode = 200;

        final NakadiTestService.Builder builder = new NakadiTestService.Builder();
        service = builder.withHost(HOST)
                         .withPort(PORT)
                         .withHandler(requestPath)
                         .withRequestMethod(requestMethod)
                         .withResponseContentType(MEDIA_TYPE)
                         .withResponseStatusCode(responseStatusCode)
                         .withResponsePayload(expectedResponseAsString)
                         .build();
        service.start();

        // check if response could be handled correctly

        final Map<String, Object> receivedMetrics = client.getMetrics();
        assertEquals("metrics data deserialiazation is not correct", expectedResponse, receivedMetrics);

        performStandardRequestChecks(requestPath, requestMethod);
    }


    @Test
    public void testGetTopics() throws Exception {

        final ArrayList<Topic> expectedResponse = Lists.newArrayList();
        Topic topic = new Topic();
        topic.setName("test-topic-1");
        expectedResponse.add(topic);

        topic = new Topic();
        topic.setName("test-topic-2");
        expectedResponse.add(topic);

        final String expectedResponseAsString = objectMapper.writeValueAsString(expectedResponse);
        final HttpString requestMethod = new HttpString("GET");
        final String requestPath = "/topics";
        final int responseStatusCode = 200;

        final NakadiTestService.Builder builder = new NakadiTestService.Builder();
        service = builder.withHost(HOST)
                .withPort(PORT)
                .withHandler(requestPath)
                .withRequestMethod(requestMethod)
                .withResponseContentType(MEDIA_TYPE)
                .withResponseStatusCode(responseStatusCode)
                .withResponsePayload(expectedResponseAsString)
                .build();
        service.start();

        final List<Topic> receivedTopics = client.getTopics();
        assertEquals("topics data deserialiazation is not correct", expectedResponse, new ArrayList<Topic>(receivedTopics));

        performStandardRequestChecks(requestPath, requestMethod);
    }


    @Test
    public void testPostEvent() throws Exception {
        final Event event = new Event();
        event.setEventType("http://test.zalando.net/my_type");
        event.setOrderingKey("ARTICLE:123456");

        final HashMap<String, String> bodyMap = Maps.newHashMap();
        bodyMap.put("greeting", "hello");
        bodyMap.put("target", "world");
        event.setBody(bodyMap);

        final HashMap<String, Object> metaDataMap = Maps.newHashMap();
        metaDataMap.put("tenant-id", "234567");
        metaDataMap.put("flow-id", "123456789");
        event.setMetadata(metaDataMap);

        final String topic = "test-topic-1";
        final HttpString requestMethod = new HttpString("POST");
        final String requestPath = "/topics/" + topic + "/events";
        final int responseStatusCode = 201;

        final NakadiTestService.Builder builder = new NakadiTestService.Builder();
        service = builder.withHost(HOST)
                .withPort(PORT)
                .withHandler(requestPath)
                .withRequestMethod(requestMethod)
                .withResponseContentType(MEDIA_TYPE)
                .withResponseStatusCode(responseStatusCode)
                .withResponsePayload("")
                .build();
        service.start();

        client.postEvent(topic, event);

        final Request request = performStandardRequestChecks(requestPath, requestMethod);
        final String requestBody = request.getRequestBody();
        final Event sentEvent = objectMapper.readValue(requestBody, Event.class);

        assertEquals("something went wrong with the event transmission", event, sentEvent);
    }

    @Test
    public void testGetPartitions() throws Exception {
        final ArrayList<TopicPartition> expectedPartitions = Lists.newArrayList();
        TopicPartition partition = new TopicPartition();
        partition.setNewestAvailableOffset("0");
        partition.setOldestAvailableOffset("0");
        partition.setPartitionId("111");
        expectedPartitions.add(partition);

        partition = new TopicPartition();
        partition.setNewestAvailableOffset("1");
        partition.setOldestAvailableOffset("0");
        partition.setPartitionId("222");
        expectedPartitions.add(partition);

        final String expectedResponse = objectMapper.writeValueAsString(expectedPartitions);


        final String topic = "test-topic-1";
        final HttpString requestMethod = new HttpString("GET");
        final String requestPath = "/topics/" + topic + "/partitions";
        final int responseStatusCode = 200;

        final NakadiTestService.Builder builder = new NakadiTestService.Builder();
        service = builder.withHost(HOST)
                .withPort(PORT)
                .withHandler(requestPath)
                .withRequestMethod(requestMethod)
                .withResponseContentType(MEDIA_TYPE)
                .withResponseStatusCode(responseStatusCode)
                .withResponsePayload(expectedResponse)
                .build();
        service.start();

        final List<TopicPartition> receivedPartitions = client.getPartitions(topic);
        assertEquals("partition data deserialization does not work properly", expectedPartitions, receivedPartitions);

        performStandardRequestChecks(requestPath, requestMethod);
    }

    @Test
    public void testGetPartition() throws Exception {

        final TopicPartition expectedPartition = new TopicPartition();
        expectedPartition.setNewestAvailableOffset("0");
        expectedPartition.setOldestAvailableOffset("0");
        expectedPartition.setPartitionId("111");

        final String expectedResponse = objectMapper.writeValueAsString(expectedPartition);


        final String partitionId = "111";
        final String topic = "test-topic-1";
        final HttpString requestMethod = new HttpString("GET");
        final String requestPath = "/topics/" + topic + "/partitions/" + partitionId;
        final int responseStatusCode = 200;

        final NakadiTestService.Builder builder = new NakadiTestService.Builder();
        service = builder.withHost(HOST)
                .withPort(PORT)
                .withHandler(requestPath)
                .withRequestMethod(requestMethod)
                .withResponseContentType(MEDIA_TYPE)
                .withResponseStatusCode(responseStatusCode)
                .withResponsePayload(expectedResponse)
                .build();
        service.start();

        final TopicPartition receivedPartition = client.getPartition(topic, partitionId);
        assertEquals("partition data deserialization does not work properly", expectedPartition, receivedPartition);

        performStandardRequestChecks(requestPath, requestMethod);
    }


    @Test
    public void testSubscibeToTopic() throws Exception {

        final ArrayList<TopicPartition> partitions = Lists.newArrayList();
        TopicPartition partition = new TopicPartition();
        partition.setNewestAvailableOffset("0");
        partition.setOldestAvailableOffset("0");
        partition.setPartitionId("p1");
        partitions.add(partition);

        partition = new TopicPartition();
        partition.setNewestAvailableOffset("1");
        partition.setOldestAvailableOffset("1");
        partition.setPartitionId("p2");
        partitions.add(partition);

        final String partitionsAsString = objectMapper.writeValueAsString(partitions);


        final Event event = new Event();
        event.setEventType("partition1");
        event.setOrderingKey("ARTICLE:123456");
        final HashMap<String, String> bodyMap = Maps.newHashMap();
        bodyMap.put("greeting", "hello");
        bodyMap.put("target", "world");
        event.setBody(bodyMap);
        final HashMap<String, Object> metaDataMap = Maps.newHashMap();
        metaDataMap.put("tenant-id", "234567");
        metaDataMap.put("flow-id", "123456789");
        event.setMetadata(metaDataMap);

        final Cursor cursor = new Cursor();
        cursor.setPartition("p1");
        cursor.setOffset("0");

        final SimpleStreamEvent streamEvent1 = new SimpleStreamEvent();
        streamEvent1.setEvents(Lists.newArrayList(event));
        streamEvent1.setCursor(cursor);

        final String streamEvent1AsString = objectMapper.writeValueAsString(streamEvent1)  + "\n";

        //----

        final Event event2 = new Event();
        event2.setEventType("partition1");
        event2.setOrderingKey("ARTICLE:123456");
        final HashMap<String, String> bodyMap2 = Maps.newHashMap();
        bodyMap2.put("greeting", "hello");
        bodyMap2.put("target", "world");
        event2.setBody(bodyMap2);
        final HashMap<String, Object> metaDataMap2 = Maps.newHashMap();
        metaDataMap2.put("tenant-id", "234567");
        metaDataMap2.put("flow-id", "123456789");
        event2.setMetadata(metaDataMap2);

        final Cursor cursor2 = new Cursor();
        cursor2.setPartition("p1");
        cursor2.setOffset("0");

        final SimpleStreamEvent streamEvent2 = new SimpleStreamEvent();
        streamEvent1.setEvents(Lists.newArrayList(event2));
        streamEvent1.setCursor(cursor2);

        final String streamEvent2AsString = objectMapper.writeValueAsString(streamEvent2) + "\n";

        //----

        final NakadiTestService.Builder builder = new NakadiTestService.Builder();
        service = builder.withHost(HOST)
                .withPort(PORT)
                .withHandler("/topics/test-topic-1/partitions")
                .withRequestMethod(new HttpString("GET"))
                .withResponseContentType(MEDIA_TYPE)
                .withResponseStatusCode(200)
                .withResponsePayload(partitionsAsString)
                .and()
                .withHandler("/topics/test-topic-1/partitions/p1/events")
                .withRequestMethod(new HttpString("GET"))
                .withResponseContentType(MEDIA_TYPE)
                .withResponseStatusCode(200)
                .withResponsePayload(streamEvent1AsString)
                .and()
                .withHandler("/topics/test-topic-1/partitions/p2/events")
                .withRequestMethod(new HttpString("GET"))
                .withResponseContentType(MEDIA_TYPE)
                .withResponseStatusCode(200)
                .withResponsePayload(streamEvent2AsString)
                .build();
        service.start();

        final ArrayList<Event> receivedEvents = Lists.newArrayList();

        final List<Future> futures = client.subscribeToTopic("test-topic-1", (c,e) -> receivedEvents.add(e)  );

        futures.forEach(f -> {
            try {
                f.get();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });

        assertTrue("one event got lost", receivedEvents.contains(event));
        assertTrue("one event got lost", receivedEvents.contains(event2));
        final Map<String, Collection<Request>> collectedRequests = service.getCollectedRequests();
        assertEquals("unexpected number of requests", 3, collectedRequests.size());

        // TODO check header and query parameters

    }
}
