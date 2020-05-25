package com.damdamdeo.eventdataspreader.writeside.aggregate;

import com.damdamdeo.eventdataspreader.writeside.aggregate.metadata.UserAggregateRootEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.api.Gift;
import com.damdamdeo.eventdataspreader.writeside.aggregate.payload.GiftAggregateRootGiftBoughtAggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.aggregate.payload.GiftAggregateRootGiftOfferedAggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.command.BuyGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.command.OfferGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;

import java.util.Objects;

public class GiftAggregateRoot extends AggregateRoot implements Gift {

    private String name;

    private String offeredTo;

    public GiftAggregateRoot() {}

    public GiftAggregateRoot(final String aggregateRootId,
                             final String name,
                             final String offeredTo,
                             final Long version) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.name = Objects.requireNonNull(name);
        this.offeredTo = offeredTo;
        this.version = Objects.requireNonNull(version);
    }

    public void handle(final BuyGiftCommand buyGiftCommand) {
        apply(new GiftAggregateRootGiftBoughtAggregateRootEventPayload(buyGiftCommand.name()),
                new UserAggregateRootEventMetadata(buyGiftCommand.executedBy()));
    }

    public void handle(final OfferGiftCommand offerGiftCommand) {
        apply(new GiftAggregateRootGiftOfferedAggregateRootEventPayload(offerGiftCommand.name(), offerGiftCommand.offeredTo()),
                new UserAggregateRootEventMetadata(offerGiftCommand.executedBy()));
    }

    public void on(final GiftAggregateRootGiftBoughtAggregateRootEventPayload giftBoughtPayload) {
        this.name = giftBoughtPayload.name();
    }

    public void on(final GiftAggregateRootGiftOfferedAggregateRootEventPayload giftOfferedPayload) {
        this.offeredTo = giftOfferedPayload.offeredTo();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String offeredTo() {
        return offeredTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GiftAggregateRoot)) return false;
        GiftAggregateRoot that = (GiftAggregateRoot) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(offeredTo, that.offeredTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, offeredTo);
    }

    @Override
    public String toString() {
        return "GiftAggregateRoot{" +
                "name='" + name + '\'' +
                ", offeredTo='" + offeredTo + '\'' +
                ", aggregateRootId='" + aggregateRootId + '\'' +
                ", version=" + version +
                '}';
    }

}
