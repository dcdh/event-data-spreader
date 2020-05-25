package com.damdamdeo.eventdataspreader.writeside.aggregate.payload;

import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayload;

import java.util.Objects;

public final class GiftAggregateRootGiftOfferedAggregateRootEventPayload extends AggregateRootEventPayload<GiftAggregateRoot> {

    private final String name;

    private final String offeredTo;

    public GiftAggregateRootGiftOfferedAggregateRootEventPayload(final String name, final String offeredTo) {
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
    public void apply(final GiftAggregateRoot giftAggregateRoot) {
        giftAggregateRoot.on(this);
    }

    @Override
    public String eventPayloadName() {
        return "GiftAggregateRootGiftOfferedAggregateRootEventPayload";
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
        GiftAggregateRootGiftOfferedAggregateRootEventPayload that = (GiftAggregateRootGiftOfferedAggregateRootEventPayload) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(offeredTo, that.offeredTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, offeredTo);
    }
}
