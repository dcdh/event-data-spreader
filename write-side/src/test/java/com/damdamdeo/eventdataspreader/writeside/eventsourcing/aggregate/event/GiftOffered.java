package com.damdamdeo.eventdataspreader.writeside.eventsourcing.aggregate.event;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventPayload;

import java.util.Objects;

public final class GiftOffered extends EventPayload<GiftAggregate> {

    private final String name;

    private final String offeredTo;

    public GiftOffered(final String name,
                       final String offeredTo) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GiftOffered)) return false;
        GiftOffered that = (GiftOffered) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(offeredTo, that.offeredTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, offeredTo);
    }

    @Override
    public String toString() {
        return "GiftOffered{" +
                "offeredTo='" + offeredTo + '\'' +
                '}';
    }

}
