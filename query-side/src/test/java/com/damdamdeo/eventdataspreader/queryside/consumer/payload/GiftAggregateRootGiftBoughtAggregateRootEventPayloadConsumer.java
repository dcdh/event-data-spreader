package com.damdamdeo.eventdataspreader.queryside.consumer.payload;

import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventPayloadConsumer;

import java.util.Objects;

public final class GiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer extends AggregateRootEventPayloadConsumer {

    private final String name;

    public GiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer(final String name) {
        this.name = Objects.requireNonNull(name);
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer that = (GiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
