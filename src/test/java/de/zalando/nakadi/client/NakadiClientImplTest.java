package de.zalando.nakadi.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.zalando.nakadi.client.domain.Topic;
import de.zalando.nakadi.client.utils.NakadiTestService;
import de.zalando.nakadi.client.utils.Request;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.*;

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


    private void performStandardRequestChecks(final String expectedRequestPath, final HttpString expectedRequestMethod){

        final Collection<Request> collectedRequests = service.getCollectedRequests();
        assertEquals("unexpected number of requests", 1, collectedRequests.size());
        final Request request = Iterators.getLast(collectedRequests.iterator());

        assertEquals("invalid request path. test must be buggy", expectedRequestPath, request.getRequestPath());
        assertEquals("invalid request method used by request", expectedRequestMethod, request.getRequestMethod());

        final HeaderMap headerMap = request.getRequestHeaders();
        HeaderValues headerValues = headerMap.get(Headers.CONTENT_TYPE);
        final String mediaType = headerValues.getFirst();
        assertEquals("invalid media type in request", mediaType, MEDIA_TYPE);

        headerValues = headerMap.get(Headers.AUTHORIZATION);
        final String authorizationHeaderValue = headerValues.getFirst();
        assertEquals("Authorization header in request is not set or invalid","Bearer " + TOKEN, authorizationHeaderValue);
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
                         .withRequestPath(requestPath)
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
                .withRequestPath(requestPath)
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


}
