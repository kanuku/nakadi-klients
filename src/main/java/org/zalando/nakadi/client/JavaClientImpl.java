package org.zalando.nakadi.client;

import play.api.libs.json.Reads;
import play.api.libs.json.Writes;
import play.libs.Json;
import scala.Option;
import scala.collection.immutable.List;
import scala.collection.immutable.Map;
import scala.util.Either;

import java.util.concurrent.Future;


class JavaClientImpl implements Klient {

    @Override
    public scala.concurrent.Future<Either<String, Map<String, Object>>> getMetrics() {
        return null;
    }

    @Override
    public scala.concurrent.Future<Either<String, List<Topic>>> getTopics() {
        return null;
    }

    @Override
    public scala.concurrent.Future<Either<String, List<TopicPartition>>> getPartitions(String topic) {
        return null;
    }

    @Override
    public scala.concurrent.Future<Option<String>> postEvent(String topic, Event event) {
        return null;
    }

    @Override
    public scala.concurrent.Future<Either<String, TopicPartition>> getPartition(String topic, String partitionId) {
        return null;
    }

    @Override
    public scala.concurrent.Future<Option<String>> postEventToPartition(String topic, String partitionId, Event event) {
        return null;
    }

    @Override
    public boolean listenForEvents$default$5() {
        return false;
    }

    @Override
    public void listenForEvents(String topic, String partitionId, ListenParameters parameters, Listener listener, boolean autoReconnect) {

    }

    @Override
    public boolean subscribeToTopic$default$4() {
        return false;
    }

    @Override
    public void subscribeToTopic(String topic, ListenParameters parameters, Listener listener, boolean autoReconnect) {

    }

    @Override
    public void stop() {

    }
}
