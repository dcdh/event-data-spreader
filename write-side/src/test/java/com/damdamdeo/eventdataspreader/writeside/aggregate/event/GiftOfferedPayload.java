package com.damdamdeo.eventdataspreader.writeside.aggregate.event;

import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventPayload;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventPayloadIdentifier;

import java.util.Objects;

public final class GiftOfferedPayload extends EventPayload<GiftAggregate> {

    private final String name;

    private final String offeredTo;

    public GiftOfferedPayload(final String name,
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
    public EventPayloadIdentifier eventPayloadIdentifier() {
        return new DefaultEventPayloadIdentifier(name,
                EventPayloadTypeEnum.GIFT_OFFERED_GIFT_PAYLOAD);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GiftOfferedPayload)) return false;
        GiftOfferedPayload that = (GiftOfferedPayload) o;
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
