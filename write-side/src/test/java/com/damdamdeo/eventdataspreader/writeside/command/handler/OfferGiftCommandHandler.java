package com.damdamdeo.eventdataspreader.writeside.command.handler;

import com.damdamdeo.eventdataspreader.writeside.command.api.*;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.command.OfferGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootRepository;

import javax.enterprise.context.Dependent;
import java.util.Objects;

@Dependent
public class OfferGiftCommandHandler extends AbstractCommandHandler<GiftAggregate, OfferGiftCommand> {

    final AggregateRootRepository aggregateRootRepository;

    public OfferGiftCommandHandler(final AggregateRootRepository aggregateRootRepository, final CommandExecutor commandExecutor) {
        super(commandExecutor);
        this.aggregateRootRepository = Objects.requireNonNull(aggregateRootRepository);
    }

    @Override
    protected GiftAggregate handle(final OfferGiftCommand command) {
        final GiftAggregate giftAggregate = aggregateRootRepository.load(command.name(), GiftAggregate.class);
        giftAggregate.handle(command);
        return aggregateRootRepository.save(giftAggregate);
    }
}
