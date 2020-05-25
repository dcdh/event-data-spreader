package com.damdamdeo.eventdataspreader.writeside.command.handler;

import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.command.BuyGiftCommand;
import com.damdamdeo.eventdataspreader.writeside.command.api.CommandExecutorBinding;
import com.damdamdeo.eventdataspreader.writeside.command.api.CommandHandler;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@CommandExecutorBinding// FIXME fine a way to be dynamic !!!
@ApplicationScoped
public class BuyGiftCommandHandler implements CommandHandler<GiftAggregateRoot, BuyGiftCommand> {

    final AggregateRootRepository aggregateRootRepository;
    final GiftAggregateRootProvider giftAggregateRootProvider;

    public BuyGiftCommandHandler(final AggregateRootRepository aggregateRootRepository,
                                 final GiftAggregateRootProvider giftAggregateRootProvider) {
        this.aggregateRootRepository = Objects.requireNonNull(aggregateRootRepository);
        this.giftAggregateRootProvider = Objects.requireNonNull(giftAggregateRootProvider);
    }

    @Override
    public GiftAggregateRoot execute(final BuyGiftCommand command) {
        final GiftAggregateRoot giftAggregateRoot = giftAggregateRootProvider.create();
        giftAggregateRoot.handle(command);
        return aggregateRootRepository.save(giftAggregateRoot);
    }

}
