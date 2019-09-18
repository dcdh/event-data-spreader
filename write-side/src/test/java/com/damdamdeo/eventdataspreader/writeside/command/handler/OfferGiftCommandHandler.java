package com.damdamdeo.eventdataspreader.writeside.command.handler;

import com.damdamdeo.eventdataspreader.writeside.command.api.CommandHandler;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregateRepository;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.GiftEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.GiftOffered;
import com.damdamdeo.eventdataspreader.writeside.command.OfferGiftCommand;

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
