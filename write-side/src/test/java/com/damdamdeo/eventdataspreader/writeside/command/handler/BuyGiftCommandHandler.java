package com.damdamdeo.eventdataspreader.writeside.command.handler;

import com.damdamdeo.eventdataspreader.writeside.command.api.AbstractCommandHandler;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregateRepository;
import com.damdamdeo.eventdataspreader.writeside.command.BuyGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.command.api.CommandExecutor;

import javax.enterprise.context.Dependent;
import java.util.Objects;

@Dependent
public class BuyGiftCommandHandler extends AbstractCommandHandler<GiftAggregate, BuyGiftCommand> {

    final GiftAggregateRepository giftAggregateRepository;

    public BuyGiftCommandHandler(final GiftAggregateRepository giftAggregateRepository, final CommandExecutor commandExecutor) {
        super(commandExecutor);
        this.giftAggregateRepository = Objects.requireNonNull(giftAggregateRepository);
    }

    @Override
    protected GiftAggregate handle(final BuyGiftCommand command) {
        final GiftAggregate giftAggregate = new GiftAggregate();
        giftAggregate.handle(command);
        return giftAggregateRepository.save(giftAggregate);
    }

}
