package de.zalando.nakadi.client.domain;


import com.google.common.base.MoreObjects;

public final class Topic {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .toString();
    }
}
