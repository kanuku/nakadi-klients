package org.zalando.nakadi.client;

import play.api.libs.json.Reads;
import play.api.libs.json.Writes;
import play.libs.Json;
import scala.Option;
import scala.collection.immutable.List;
import scala.collection.immutable.Map;
import scala.util.Either;

import java.util.concurrent.Future;


class JavaClientImpl  {

    private final Klient scalaClient;

    public JavaClientImpl(final Klient scalaClient){
        this.scalaClient = scalaClient;
    }


    public Future<Either<String, Map<String, Object>>> getMetrics() {
        //getMetrics().get().right().get().
        return Utils.convert(scalaClient.getMetrics());
    }

    public Future<Either<String, List<Topic>>> getTopics(Reads<List<Topic>> reader) {
        return null;
    }

    @Override
    public Future<Either<String, List<TopicPartition>>> getPartitions(String topic, Reads<List<TopicPartition>> reader) {
        return null;
    }

    @Override
    public Future<Option<String>> postEvent(String topic, Event event, Writes<Event> writer) {
        return null;
    }

    @Override
    public Future<Either<String, TopicPartition>> getPartition(String topic, String partitionId, Reads<TopicPartition> reader) {
        return null;
    }

    @Override
    public Future<Option<String>> postEventToPartition(String topic, String partitionId, Event event, Writes<Event> writer) {
        return null;
    }

    @Override
    public boolean listenForEvents$default$5() {
        return false;
    }

    @Override
    public void listenForEvents(String topic, String partitionId, ListenParameters parameters, Listener listener, boolean autoReconnect, Reads<SimpleStreamEvent> reader) {

    }

    @Override
    public boolean subscribeToTopic$default$4() {
        return false;
    }

    @Override
    public void subscribeToTopic(String topic, ListenParameters parameters, Listener listener, boolean autoReconnect, Reads<SimpleStreamEvent> reader) {

    }

    @Override
    public void stop() {

    }
}
