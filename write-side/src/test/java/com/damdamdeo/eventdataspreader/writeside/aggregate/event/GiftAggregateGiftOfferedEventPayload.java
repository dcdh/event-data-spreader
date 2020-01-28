package com.damdamdeo.eventdataspreader.writeside.aggregate.event;

import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayload;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class GiftAggregateGiftOfferedEventPayload extends AggregateRootEventPayload<GiftAggregate> {

    private final String name;

    private final String offeredTo;

    @JsonCreator
    public GiftAggregateGiftOfferedEventPayload(@JsonProperty("name") final String name,
                                                @JsonProperty("offeredTo") final String offeredTo) {
        this.name = Objects.requireNonNull(name);
        this.offeredTo = Objects.requireNonNull(offeredTo);
    }

    public String name() {
        return name;
    }

    public String offeredTo() {
        return offeredTo;
    }

    @Override
    protected void apply(final GiftAggregate giftAggregate) {
        giftAggregate.on(this);
    }

    @Override
    public String eventName() {
        return "GiftOffered";
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
        if (!(o instanceof GiftAggregateGiftOfferedEventPayload)) return false;
        GiftAggregateGiftOfferedEventPayload that = (GiftAggregateGiftOfferedEventPayload) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(offeredTo, that.offeredTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, offeredTo);
    }

    @Override
    public String toString() {
        return "GiftOfferedPayload{" +
                "name='" + name + '\'' +
                ", offeredTo='" + offeredTo + '\'' +
                '}';
    }
}
