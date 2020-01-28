package com.damdamdeo.eventdataspreader.writeside.query.event;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventPayload;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class GiftAggregateGiftBoughtEventPayload implements EventPayload {

    private final String name;

    @JsonCreator
    public GiftAggregateGiftBoughtEventPayload(@JsonProperty("name") final String name) {
        this.name = Objects.requireNonNull(name);
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GiftAggregateGiftBoughtEventPayload)) return false;
        GiftAggregateGiftBoughtEventPayload that = (GiftAggregateGiftBoughtEventPayload) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "GiftAggregateGiftBoughtEventPayload{" +
                "name='" + name + '\'' +
                '}';
    }
}
