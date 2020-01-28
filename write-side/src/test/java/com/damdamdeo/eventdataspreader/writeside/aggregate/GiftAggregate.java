package com.damdamdeo.eventdataspreader.writeside.aggregate;

import com.damdamdeo.eventdataspreader.writeside.aggregate.event.DefaultEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.command.BuyGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.command.OfferGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.GiftAggregateGiftBoughtEventPayload;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.GiftAggregateGiftOfferedEventPayload;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GiftAggregate extends AggregateRoot {

    private String name;

    private String offeredTo;

    public GiftAggregate() {}

    @JsonCreator
    public GiftAggregate(@JsonProperty("aggregateRootId") final String aggregateRootId,
                         @JsonProperty("name") final String name,
                         @JsonProperty("offeredTo") final String offeredTo,
                         @JsonProperty("version") final Long version) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.name = Objects.requireNonNull(name);
        this.offeredTo = offeredTo;
        this.version = Objects.requireNonNull(version);
    }

    public void handle(final BuyGiftCommand buyGiftCommand) {
        apply(new GiftAggregateGiftBoughtEventPayload(buyGiftCommand.name()),
                new DefaultEventMetadata(buyGiftCommand.executedBy()));
    }

    public void handle(final OfferGiftCommand offerGiftCommand) {
        apply(new GiftAggregateGiftOfferedEventPayload(offerGiftCommand.name(), offerGiftCommand.offeredTo()),
                new DefaultEventMetadata(offerGiftCommand.executedBy()));
    }

    public void on(final GiftAggregateGiftBoughtEventPayload giftBoughtPayload) {
        this.name = giftBoughtPayload.name();
    }

    public void on(final GiftAggregateGiftOfferedEventPayload giftOfferedPayload) {
        this.offeredTo = giftOfferedPayload.offeredTo();
    }

    public String name() {
        return name;
    }

    public String offeredTo() {
        return offeredTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GiftAggregate)) return false;
        GiftAggregate that = (GiftAggregate) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(offeredTo, that.offeredTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, offeredTo);
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
