package com.damdamdeo.eventdataspreader.writeside.aggregate.event;

import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventPayload;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventPayloadIdentifier;

import java.util.Objects;

public final class GiftBoughtPayload extends EventPayload<GiftAggregate> {

    private final String name;

    public GiftBoughtPayload(final String name) {
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
    public EventPayloadIdentifier eventPayloadIdentifier() {
        return new DefaultEventPayloadIdentifier(name,
                EventPayloadTypeEnum.GIFT_BOUGHT_GIFT_PAYLOAD);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GiftBoughtPayload)) return false;
        GiftBoughtPayload that = (GiftBoughtPayload) o;
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
