package de.zalando.nakadi.client.utils;


import com.google.common.base.MoreObjects;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;

import java.util.Collection;
import java.util.Deque;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class NakadiTestService {

    private final String host;
    private final int port;
    private final String requestPath;
    private final HttpString requestMethod;
    private final int responseStatusCode;
    private final String responsePayload;
    private final String responseContentType;

    private ConcurrentLinkedDeque<Request> collectedRequests;

    private Undertow server;

    private NakadiTestService(final String host,
                              final int port,
                              final String requestPath,
                              final HttpString requestMethod,
                              final int responseStatusCode,
                              final String responsePayload,
                              final String responseContentType) {
        this.host = host;
        this.port = port;
        this.requestPath = requestPath;
        this.requestMethod = requestMethod;
        this.responseStatusCode = responseStatusCode;
        this.responsePayload = responsePayload;
        this.responseContentType = responseContentType;

        this.collectedRequests = new ConcurrentLinkedDeque<>();
    }


    public void start() {
        server = Undertow.builder()
                .addHttpListener(port, host)
                .setHandler(new HttpHandler() {

                    @Override
                    public void handleRequest(final HttpServerExchange exchange) throws Exception {

                        final String expectedRequestPath = NakadiTestService.this.requestPath;
                        final String receivedRequestPath = exchange.getRequestPath();

                        final HttpString expectedRequestMethod =  NakadiTestService.this.requestMethod;
                        final HttpString receivedRequestMethod =  exchange.getRequestMethod();


                        if ( Objects.equals(expectedRequestMethod, receivedRequestMethod) &&
                             Objects.equals(expectedRequestPath, receivedRequestPath)) {

                            final HeaderMap requestHeaders = exchange.getRequestHeaders();
                            final Map<String, Deque<String>> requestQueryParameters = exchange.getQueryParameters();
                            final HttpString requestMethod = exchange.getRequestMethod();

                            exchange.startBlocking();
                            final String requestBody = IOUtils.toString(exchange.getInputStream());

                            final Request request = new Request(requestPath,
                                                                requestHeaders,
                                                                requestQueryParameters,
                                                                requestMethod,
                                                                requestBody);
                            collectedRequests.add(request);

                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, responseContentType);
                            exchange.setResponseCode(responseStatusCode);
                            exchange.getResponseSender().send(responsePayload);
                        } else {
                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, responseContentType);
                            exchange.setResponseCode(HttpStatus.SC_NOT_FOUND);
                            exchange.getResponseSender().send("invalid request: " + receivedRequestPath + " with request methid: " + requestMethod);
                        }
                    }
                }).build();
        server.start();
    }


    public Collection<Request> getCollectedRequests() {
        return new ConcurrentLinkedDeque<>(this.collectedRequests);
    }

    public void stop() {
        server.stop();
        server = null;
    }


    //-------------


    public static class Builder {

        private String host = "localhost";
        private int port = 8080;
        private String requestPath = "/notSet";
        private HttpString requestMethod;
        private int responseStatusCode = 500;
        private String responsePayload = "{ \"content\": \"nix\" }";
        private String responseContentType = "application/json";


        public Builder withHost(final String host) {
            this.host = checkNotNull(host);
            return this;
        }

        public Builder withPort(final int port) {
            this.port = port;
            return this;
        }


        public Builder withRequestPath(final String requestPath) {
            this.requestPath = checkNotNull(requestPath);
            return this;
        }

        public Builder withRequestMethod(final HttpString requestMethod) {
            this.requestMethod = checkNotNull(requestMethod);
            return this;
        }


        public Builder withResponseStatusCode(final int responsStatusCode) {
            checkArgument(responsStatusCode > 0 && responsStatusCode < 1000,
                    "invalid response status code %s", responsStatusCode);
            this.responseStatusCode = responsStatusCode;
            return this;
        }

        public Builder withResponsePayload(final String responsePayload) {
            this.responsePayload = checkNotNull(responsePayload);
            return this;
        }

        public Builder withResponseContentType(final String responseContentType) {
            this.responseContentType = checkNotNull(responseContentType);
            return this;
        }


        public NakadiTestService build() {
            return new NakadiTestService(host,
                    port,
                    requestPath,
                    requestMethod,
                    responseStatusCode,
                    responsePayload,
                    responseContentType);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("host", host)
                    .add("port", port)
                    .add("requestPath", requestPath)
                    .add("requestMethod", requestMethod)
                    .add("responseStatusCode", responseStatusCode)
                    .add("responsePayload", responsePayload)
                    .add("responseContentType", responseContentType)
                    .toString();
        }
    }
}
