package org.zalando.nakadi.client.java.test.event.generator;

import java.util.*;

import org.zalando.nakadi.client.java.*;
import org.zalando.nakadi.client.java.model.*;

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
