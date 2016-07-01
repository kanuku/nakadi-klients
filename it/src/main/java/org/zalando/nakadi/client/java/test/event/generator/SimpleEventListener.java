package org.zalando.nakadi.client.java.test.event.generator;

import java.util.List;
import java.util.Optional;

import org.zalando.nakadi.client.java.ClientError;
import org.zalando.nakadi.client.java.Listener;
import org.zalando.nakadi.client.java.model.Cursor;
import org.zalando.nakadi.client.java.model.Event;

public class SimpleEventListener   implements Listener<Event>{

    @Override
    public String getId() {
        // TODO Auto-generated method stub
        return null;
    }

    

    @Override
    public void onSubscribed(String endpoint, Optional<Cursor> cursor) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onError(String endpoint, Optional<ClientError> error) {
        // TODO Auto-generated method stub
        
    }



    @Override
    public void onReceive(String endpoint, Cursor cursor, List<Event> events) {
        // TODO Auto-generated method stub
        
    }

 
}
