package com.damdamdeo.eventdataspreader.writeside.aggregate.payload;

import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayload;

import java.util.Objects;

public final class GiftAggregateRootGiftBoughtAggregateRootEventPayload extends AggregateRootEventPayload<GiftAggregateRoot> {

    private final String name;

    public GiftAggregateRootGiftBoughtAggregateRootEventPayload(final String name) {
        this.name = Objects.requireNonNull(name);
    }

    public String name() {
        return name;
    }

    @Override
    public void apply(final GiftAggregateRoot giftAggregateRoot) {
        giftAggregateRoot.on(this);
    }

    @Override
    public String eventPayloadName() {
        return "GiftAggregateRootGiftBoughtAggregateRootEventPayload";
    }

    @Override
    public String aggregateRootId() {
        return name;
    }

    @Override
    public String aggregateRootType() {
        return "GiftAggregateRoot";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GiftAggregateRootGiftBoughtAggregateRootEventPayload that = (GiftAggregateRootGiftBoughtAggregateRootEventPayload) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
