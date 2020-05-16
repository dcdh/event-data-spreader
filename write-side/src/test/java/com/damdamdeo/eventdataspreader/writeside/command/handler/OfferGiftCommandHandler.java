package com.damdamdeo.eventdataspreader.writeside.command.handler;

import com.damdamdeo.eventdataspreader.writeside.command.api.*;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.command.OfferGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@CommandExecutorBinding// FIXME fine a way to be dynamic !!!
@ApplicationScoped
public class OfferGiftCommandHandler implements CommandHandler<GiftAggregate, OfferGiftCommand> {

    final AggregateRootRepository aggregateRootRepository;

    public OfferGiftCommandHandler(final AggregateRootRepository aggregateRootRepository) {
        this.aggregateRootRepository = Objects.requireNonNull(aggregateRootRepository);
    }

    @Override
    public GiftAggregate execute(final OfferGiftCommand command) {
        final GiftAggregate giftAggregate = aggregateRootRepository.load(command.name(), GiftAggregate.class);
        giftAggregate.handle(command);
        return aggregateRootRepository.save(giftAggregate);
    }
}
