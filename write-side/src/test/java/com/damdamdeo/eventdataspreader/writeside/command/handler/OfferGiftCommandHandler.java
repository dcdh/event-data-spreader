package com.damdamdeo.eventdataspreader.writeside.command.handler;

import com.damdamdeo.eventdataspreader.writeside.command.api.*;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregateRepository;
import com.damdamdeo.eventdataspreader.writeside.command.OfferGiftCommand;

import javax.enterprise.context.Dependent;
import java.util.Objects;

@Dependent
public class OfferGiftCommandHandler extends AbstractCommandHandler<GiftAggregate, OfferGiftCommand> {

    final GiftAggregateRepository giftAggregateRepository;

    public OfferGiftCommandHandler(final GiftAggregateRepository giftAggregateRepository, final CommandExecutor commandExecutor) {
        super(commandExecutor);
        this.giftAggregateRepository = Objects.requireNonNull(giftAggregateRepository);
    }

    @Override
    protected GiftAggregate handle(final OfferGiftCommand command) {
        final GiftAggregate giftAggregate = giftAggregateRepository.load(command.name());
        giftAggregate.handle(command);
        return giftAggregateRepository.save(giftAggregate);
    }
}
