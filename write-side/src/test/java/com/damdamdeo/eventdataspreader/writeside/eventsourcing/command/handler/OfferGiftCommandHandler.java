package com.damdamdeo.eventdataspreader.writeside.eventsourcing.command.handler;

import com.damdamdeo.eventdataspreader.writeside.command.api.CommandHandler;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.aggregate.GiftAggregateRepository;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.aggregate.event.GiftEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.aggregate.event.GiftOffered;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.command.OfferGiftCommand;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class OfferGiftCommandHandler implements CommandHandler<GiftAggregate, OfferGiftCommand> {

    @Inject
    GiftAggregateRepository giftAggregateRepository;

    @Override
    public GiftAggregate handle(final OfferGiftCommand command) {
        final GiftAggregate giftAggregate = giftAggregateRepository.load(command.aggregateId());
        giftAggregate.apply(new GiftOffered(command.name(), command.offeredTo()),
                new GiftEventMetadata(command.executedBy()));
        return giftAggregateRepository.save(giftAggregate);
    }

}
