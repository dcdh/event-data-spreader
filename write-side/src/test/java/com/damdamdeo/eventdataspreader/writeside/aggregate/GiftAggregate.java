package com.damdamdeo.eventdataspreader.writeside.aggregate;

import com.damdamdeo.eventdataspreader.writeside.aggregate.event.DefaultEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.command.BuyGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.command.OfferGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.GiftBoughtPayload;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.GiftOfferedPayload;

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

    public void handle(final BuyGiftCommand buyGiftCommand) {
        apply(new GiftBoughtPayload(buyGiftCommand.name()),
                new DefaultEventMetadata(buyGiftCommand.executedBy()));
    }

    public void handle(final OfferGiftCommand offerGiftCommand) {
        apply(new GiftOfferedPayload(offerGiftCommand.name(), offerGiftCommand.offeredTo()),
                new DefaultEventMetadata(offerGiftCommand.executedBy()));
    }

    public void on(final GiftBoughtPayload giftBoughtPayload) {
        this.name = giftBoughtPayload.name();
    }

    public void on(final GiftOfferedPayload giftOfferedPayload) {
        this.offeredTo = giftOfferedPayload.offeredTo();
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
