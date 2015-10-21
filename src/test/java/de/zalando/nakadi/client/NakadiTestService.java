package de.zalando.nakadi.client;


import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.apache.http.HttpStatus;

public class NakadiTestService {

    private Undertow server;

    public NakadiTestService() {

    }


    private static final String METRICS = "{\n" +
            "  \"get_metrics\": {\n" +
            "    \"calls_per_second\": 0.0011111111111111111,\n" +
            "    \"count\": 1,\n" +
            "    \"status_codes\": {\n" +
            "      \"401\": 1\n" +
            "    }\n" +
            "  },\n" +
            "  \"post_event\": {\n" +
            "    \"calls_per_second\": 0.005555555555555556,\n" +
            "    \"count\": 5,\n" +
            "    \"status_codes\": {\n" +
            "      \"201\": 5\n" +
            "    }\n" +
            "  }";


    public void start() {
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(final HttpServerExchange exchange) throws Exception {

                        final String requestPath = exchange.getRequestPath();

                        switch (requestPath) {
                            case "/metrics":
                                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                                exchange.getResponseSender().send(METRICS);
                                exchange.setResponseCode(HttpStatus.SC_OK);
                                break;
                        }

                    }
                }).build();
        server.start();
    }


    public void stop() {
        server.stop();
        server = null;
    }
}
