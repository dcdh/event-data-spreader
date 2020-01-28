package com.damdamdeo.eventdataspreader.writeside.aggregate.event;

import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayload;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class GiftAggregateGiftBoughtEventPayload extends AggregateRootEventPayload<GiftAggregate> {

    private final String name;

    @JsonCreator
    public GiftAggregateGiftBoughtEventPayload(@JsonProperty("name") final String name) {
        this.name = Objects.requireNonNull(name);
    }

    public String name() {
        return name;
    }

    @Override
    protected void apply(final GiftAggregate giftAggregate) {
        giftAggregate.on(this);
    }

    @Override
    public String eventName() {
        return "GiftBought";
    }

    @Override
    public String aggregateRootId() {
        return name;
    }

    @Override
    public String aggregateRootType() {
        return "GiftAggregate";
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
        return "GiftBoughtPayload{" +
                "name='" + name + '\'' +
                '}';
    }
}
