package com.damdamdeo.eventdataspreader.writeside.command.handler;

import com.damdamdeo.eventdataspreader.writeside.aggregate.event.DefaultEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.command.api.Command;
import com.damdamdeo.eventdataspreader.writeside.command.api.CommandHandler;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregateRepository;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.GiftOffered;
import com.damdamdeo.eventdataspreader.writeside.command.OfferGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.command.api.CommandQualifier;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@CommandQualifier(OfferGiftCommand.class)
public class OfferGiftCommandHandler implements CommandHandler {

    @Inject
    GiftAggregateRepository giftAggregateRepository;

    @Override
    public AggregateRoot handle(final Command command) {
        final GiftAggregate giftAggregate = giftAggregateRepository.load(command.aggregateId());
        final OfferGiftCommand offerGiftCommand = (OfferGiftCommand) command;
        giftAggregate.apply(new GiftOffered(offerGiftCommand.name(), offerGiftCommand.offeredTo()),
                new DefaultEventMetadata(offerGiftCommand.executedBy()));
        return giftAggregateRepository.save(giftAggregate);
    }

}
