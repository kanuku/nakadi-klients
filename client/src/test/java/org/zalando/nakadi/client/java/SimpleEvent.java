package org.zalando.nakadi.client.java;

import org.zalando.nakadi.client.java.model.Event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SimpleEvent implements Event {
    private String id;

    @JsonCreator
    public SimpleEvent(@JsonProperty("id") String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SimpleEvent other = (SimpleEvent) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SimpleEvent [id=" + id + "]";
    }

    

}
