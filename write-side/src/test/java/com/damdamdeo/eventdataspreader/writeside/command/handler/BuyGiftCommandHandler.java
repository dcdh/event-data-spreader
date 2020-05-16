package com.damdamdeo.eventdataspreader.writeside.command.handler;

import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.command.BuyGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.command.api.CommandExecutorBinding;
import com.damdamdeo.eventdataspreader.writeside.command.api.CommandHandler;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@CommandExecutorBinding// FIXME fine a way to be dynamic !!!
@ApplicationScoped
public class BuyGiftCommandHandler implements CommandHandler<GiftAggregate, BuyGiftCommand> {

    final AggregateRootRepository aggregateRootRepository;
    final GiftAggregateRootProvider giftAggregateRootProvider;

    public BuyGiftCommandHandler(final AggregateRootRepository aggregateRootRepository,
                                 final GiftAggregateRootProvider giftAggregateRootProvider) {
        this.aggregateRootRepository = Objects.requireNonNull(aggregateRootRepository);
        this.giftAggregateRootProvider = Objects.requireNonNull(giftAggregateRootProvider);
    }

    @Override
    public GiftAggregate execute(final BuyGiftCommand command) {
        final GiftAggregate giftAggregate = giftAggregateRootProvider.create();
        giftAggregate.handle(command);
        return aggregateRootRepository.save(giftAggregate);
    }

}
