package org.zalando.nakadi.client.utils;


import com.google.common.collect.Maps;
import org.zalando.nakadi.client.Client;
import org.zalando.nakadi.client.Event;
import org.zalando.nakadi.client.KlientBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


import scala.collection.JavaConversions;
import scala.util.Either;


public class Main {


    public static void main(final String[] args) throws Exception {

        final Client client = new KlientBuilder()
                        .withEndpoint(new URI("192.168.99.100"))
                        .withPort(8080)
                        .withSecuredConnection(false)
                        .withJavaTokenProvider(() -> "<my token>")
                        .buildJavaClient();

        System.out.println("---> " + client.getMetrics().get());


        final HashMap<String,Object> meta = Maps.newHashMap();
        meta.put("id", "1234567890");

        final Future<Either<String, Void>> f =
                                            client.postEvent("test", new Event("eventType", "orderingKey", meta, "{}"));

        final Either<String, Void> postResult = f.get();

        if(postResult.isLeft())
            System.out.println(">>POST EVENT - LEFT>>>" + postResult.left().get());
        else
            System.out.println(">>POST EVENT - LEFT>>>" + postResult.right().get());
    }
}
