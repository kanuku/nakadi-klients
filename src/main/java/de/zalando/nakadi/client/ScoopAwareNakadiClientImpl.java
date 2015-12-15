package de.zalando.nakadi.client;

import akka.actor.ActorSystem;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import de.zalando.nakadi.client.domain.Cursor;
import de.zalando.nakadi.client.domain.Event;
import de.zalando.nakadi.client.domain.SimpleStreamEvent;
import de.zalando.scoop.Scoop;
import de.zalando.scoop.ScoopClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;


public class ScoopAwareNakadiClientImpl extends NakadiClientImpl {

    private final ObjectMapper objectMapper;
    private final ActorSystem scoopSystem;
    private final ScoopClient scoopClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(ScoopAwareNakadiClientImpl.class);

    ScoopAwareNakadiClientImpl(final URI endpoint,
                               final OAuth2TokenProvider tokenProvider,
                               final ObjectMapper objectMapper,
                               final Scoop scoop) {
        super(endpoint, tokenProvider, objectMapper);

        checkNotNull(scoop, "Scoop instance must not be null");

        this.objectMapper = objectMapper;

        this.scoopSystem = scoop.build();
        this.scoopClient = scoop.defaultClient();
    }


    @Override
    public void listenForEvents(final String topic,
                                final String partitionId,
                                final String startOffset,
                                final int batchLimit,
                                final int batchFlushTimeoutInSeconds,
                                final int streamLimit,
                                final EventListener listener) {
        checkArgument(!isNullOrEmpty(topic), "topic must not be null or empty");
        checkArgument(! isNullOrEmpty(partitionId), "partition id must not be null or empty");
        checkArgument(! isNullOrEmpty(startOffset), "start offset must not be null or empty");
        checkArgument(batchLimit > 0, "batch limit must be > 0. Given: %s", batchLimit);
        checkArgument(batchFlushTimeoutInSeconds > -1, "batch flush timeout must be > -1. Given: %s", batchFlushTimeoutInSeconds);
        checkArgument(streamLimit > -1, "stream limit must be > -1. Given: %s", streamLimit);

        final String uri = String.format(URI_EVENT_LISTENING, topic, partitionId, startOffset, batchLimit, batchFlushTimeoutInSeconds, streamLimit);
        final HttpRequestBase request = setUpRequest(HttpMethod.GET, uri);
        final CloseableHttpResponse response = performRequest(request);


        try {
            final InputStream contentInputStream = response.getEntity().getContent();
            final ByteArrayOutputStream bout = new ByteArrayOutputStream(INITIAL_RECEIVE_BUFFER_SIZE);

            SimpleStreamEvent streamingEvent;
            List<Event> events;
            int byteItem;
            while ((byteItem = contentInputStream.read()) != -1) {
                bout.write(byteItem);

                if(byteItem == EOL) {
                    streamingEvent = objectMapper.readValue(bout.toByteArray(), SimpleStreamEvent.class);
                    final Cursor cursor = streamingEvent.getCursor();
                    LOGGER.debug("received [streamingEvent={}]", streamingEvent);
                    events = streamingEvent.getEvents();
                    if(events == null || events.isEmpty()) {
                        LOGGER.debug("received [streamingEvent={}] does not contain any event ", streamingEvent);
                    }
                    else {
                        streamingEvent.getEvents().forEach( event -> {
                            try {
                                final Map<String, Object> meta = event.getMetadata();
                                final String id  = (String) meta.get("id");
                                if(id == null){
                                    LOGGER.warn("meta data 'id' is not set in [event={}] -> consuming event",event);
                                    listener.onReceive(cursor, event);
                                }
                                else if(scoopClient.isHandledByMe(id)) {
                                    LOGGER.debug("scoop: [event={}] IS handled by me", event);
                                    listener.onReceive(cursor, event);
                                }
                                else {
                                    LOGGER.debug("scoop: [event={}] is NOT handled by me", event);
                                }
                            }
                            catch(final Exception e) {
                                LOGGER.warn("a problem occurred while passing [cursor={}, event={}] to [listener={}] -> continuing with next events",
                                        cursor, event,listener, e);
                            }
                        });
                    }

                    bout.reset();
                }
            }
            LOGGER.info("stream from [response={}] retrieved from [URI={}] was closed", response, uri);
        } catch (final IOException e) {
            throw new ClientException(e);
        }
    }

}
