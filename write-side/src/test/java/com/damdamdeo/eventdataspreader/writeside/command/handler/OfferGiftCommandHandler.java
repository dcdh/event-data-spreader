package com.damdamdeo.eventdataspreader.writeside.command.handler;

import com.damdamdeo.eventdataspreader.writeside.command.api.Command;
import com.damdamdeo.eventdataspreader.writeside.command.api.CommandHandler;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregateRepository;
import com.damdamdeo.eventdataspreader.writeside.command.OfferGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.command.api.CommandQualifier;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;

import javax.enterprise.context.Dependent;
import java.util.Objects;

@Dependent
@CommandQualifier(OfferGiftCommand.class)
public class OfferGiftCommandHandler implements CommandHandler {

    final GiftAggregateRepository giftAggregateRepository;

    public OfferGiftCommandHandler(final GiftAggregateRepository giftAggregateRepository) {
        this.giftAggregateRepository = Objects.requireNonNull(giftAggregateRepository);
    }

    @Override
    public AggregateRoot handle(final Command command) {
        final GiftAggregate giftAggregate = giftAggregateRepository.load(command.aggregateId());
        final OfferGiftCommand offerGiftCommand = (OfferGiftCommand) command;
        giftAggregate.handle(offerGiftCommand);
        return giftAggregateRepository.save(giftAggregate);
    }

}
