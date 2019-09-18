package com.damdamdeo.eventdataspreader.writeside.eventsourcing.aggregate;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.aggregate.event.GiftBought;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.aggregate.event.GiftOffered;

import java.util.Objects;

public class GiftAggregate extends AggregateRoot {

    private String name;

    private String offeredTo;

    public GiftAggregate() {}

    public GiftAggregate(final String aggregateRootId,
                         final String name,
                         final String offeredTo,
                         final Long version) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.name = Objects.requireNonNull(name);
        this.offeredTo = offeredTo;
        this.version = Objects.requireNonNull(version);
    }

    public void on(final GiftBought giftBought) {
        this.aggregateRootId = giftBought.name();
        this.name = giftBought.name();
    }

    public void on(final GiftOffered giftOffered) {
        this.offeredTo = giftOffered.offeredTo();
    }

    public String name() {
        return name;
    }

    public String offeredTo() {
        return offeredTo;
    }

    @Override
    public String toString() {
        return "GiftAggregate{" +
                "name='" + name + '\'' +
                ", offeredTo='" + offeredTo + '\'' +
                ", aggregateRootId='" + aggregateRootId + '\'' +
                ", version=" + version +
                '}';
    }

}
