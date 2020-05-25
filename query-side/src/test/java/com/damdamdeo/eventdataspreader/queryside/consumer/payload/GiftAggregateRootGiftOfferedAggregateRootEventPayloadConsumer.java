package com.damdamdeo.eventdataspreader.queryside.consumer.payload;

import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventPayloadConsumer;

import java.util.Objects;

public final class GiftAggregateRootGiftOfferedAggregateRootEventPayloadConsumer extends AggregateRootEventPayloadConsumer {

    private final String name;

    private final String offeredTo;

    public GiftAggregateRootGiftOfferedAggregateRootEventPayloadConsumer(final String name, final String offeredTo) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GiftAggregateRootGiftOfferedAggregateRootEventPayloadConsumer that = (GiftAggregateRootGiftOfferedAggregateRootEventPayloadConsumer) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(offeredTo, that.offeredTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, offeredTo);
    }
}
