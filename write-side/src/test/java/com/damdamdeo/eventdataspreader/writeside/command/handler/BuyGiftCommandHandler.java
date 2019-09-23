package com.damdamdeo.eventdataspreader.writeside.command.handler;

import com.damdamdeo.eventdataspreader.writeside.command.api.Command;
import com.damdamdeo.eventdataspreader.writeside.command.api.CommandHandler;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregateRepository;
import com.damdamdeo.eventdataspreader.writeside.command.BuyGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.command.api.CommandQualifier;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
@CommandQualifier(BuyGiftCommand.class)
public class BuyGiftCommandHandler implements CommandHandler {

    final GiftAggregateRepository giftAggregateRepository;

    public BuyGiftCommandHandler(final GiftAggregateRepository giftAggregateRepository) {
        this.giftAggregateRepository = Objects.requireNonNull(giftAggregateRepository);
    }

    @Override
    public AggregateRoot handle(final Command command) {
        final GiftAggregate giftAggregate = new GiftAggregate();
        final BuyGiftCommand buyGiftCommand = (BuyGiftCommand) command;
        giftAggregate.handle(buyGiftCommand);
        return giftAggregateRepository.save(giftAggregate);
    }

}
