package com.damdamdeo.eventdataspreader.writeside.aggregate.event;

import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventPayload;

import java.util.Objects;

public final class GiftBought extends EventPayload<GiftAggregate> {

    private final String name;

    public GiftBought(final String name) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GiftBought)) return false;
        GiftBought that = (GiftBought) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "GiftBought{" +
                "name='" + name + '\'' +
                '}';
    }
}
