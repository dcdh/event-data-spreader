package com.damdamdeo.eventdataspreader.writeside.command.handler;

import com.damdamdeo.eventdataspreader.writeside.command.api.*;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.command.OfferGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@CommandExecutorBinding// FIXME fine a way to be dynamic !!!
@ApplicationScoped
public class OfferGiftCommandHandler implements CommandHandler<GiftAggregateRoot, OfferGiftCommand> {

    final AggregateRootRepository aggregateRootRepository;

    public OfferGiftCommandHandler(final AggregateRootRepository aggregateRootRepository) {
        this.aggregateRootRepository = Objects.requireNonNull(aggregateRootRepository);
    }

    @Override
    public GiftAggregateRoot execute(final OfferGiftCommand command) {
        final GiftAggregateRoot giftAggregateRoot = aggregateRootRepository.load(command.name(), GiftAggregateRoot.class);
        giftAggregateRoot.handle(command);
        return aggregateRootRepository.save(giftAggregateRoot);
    }
}
