package org.zalando.nakadi.client;


import com.google.common.base.Preconditions;
import scala.Option;

public class JListenerWrapper implements Listener {

    private final JListener listener;

    public JListenerWrapper(final JListener listener) {
        Preconditions.checkNotNull("listener must not be null");
        this.listener = listener;
    }

    @Override
    public String id() {
        return listener.id();
    }

    @Override
    public void onReceive(String topic, String partition, Cursor cursor, Event event) {
        listener.onReceive(topic, partition, cursor, new JListener.JEvent(event));
    }

    @Override
    public void onConnectionOpened(String topic, String partition) {
        listener.onConnectionOpened(topic, partition);
    }

    @Override
    public void onConnectionFailed(String topic, String partition, int status, String error) {
        listener.onConnectionFailed(topic, partition, status, error);
    }

    @Override
    public void onConnectionClosed(String topic, String partition, Option<Cursor> lastCursor) {
        listener.onConnectionClosed(topic, partition, Utils.convertToOptional(lastCursor));
    }

}
