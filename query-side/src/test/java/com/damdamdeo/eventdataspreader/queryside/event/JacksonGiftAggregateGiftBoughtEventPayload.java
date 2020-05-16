package com.damdamdeo.eventdataspreader.queryside.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class JacksonGiftAggregateGiftBoughtEventPayload implements GiftAggregateGiftBoughtEventPayload {

    private final String name;

    @JsonCreator
    public JacksonGiftAggregateGiftBoughtEventPayload(@JsonProperty("name") final String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JacksonGiftAggregateGiftBoughtEventPayload)) return false;
        JacksonGiftAggregateGiftBoughtEventPayload that = (JacksonGiftAggregateGiftBoughtEventPayload) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "JacksonGiftAggregateGiftBoughtEventPayload{" +
                "name='" + name + '\'' +
                '}';
    }
}
