package com.damdamdeo.eventdataspreader.writeside.command.handler;

import com.damdamdeo.eventdataspreader.writeside.command.api.AbstractCommandHandler;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.command.BuyGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.command.api.CommandExecutor;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootRepository;

import javax.enterprise.context.Dependent;
import java.util.Objects;

@Dependent
public class BuyGiftCommandHandler extends AbstractCommandHandler<GiftAggregate, BuyGiftCommand> {

    final AggregateRootRepository aggregateRootRepository;

    public BuyGiftCommandHandler(final AggregateRootRepository aggregateRootRepository, final CommandExecutor commandExecutor) {
        super(commandExecutor);
        this.aggregateRootRepository = Objects.requireNonNull(aggregateRootRepository);
    }

    @Override
    protected GiftAggregate handle(final BuyGiftCommand command) {
        final GiftAggregate giftAggregate = new GiftAggregate();
        giftAggregate.handle(command);
        return aggregateRootRepository.save(giftAggregate);
    }

}
