package com.damdamdeo.eventdataspreader.writeside.command.handler;

import com.damdamdeo.eventdataspreader.writeside.command.api.CommandHandler;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregate;
import com.damdamdeo.eventdataspreader.writeside.aggregate.GiftAggregateRepository;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.GiftBought;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.GiftEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.command.BuyGiftCommand;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class BuyGiftCommandHandler implements CommandHandler<GiftAggregate, BuyGiftCommand> {

    @Inject
    GiftAggregateRepository giftAggregateRepository;

    @Override
    public GiftAggregate handle(final BuyGiftCommand command) {
        final GiftAggregate giftAggregate = new GiftAggregate();
        giftAggregate.apply(new GiftBought(command.name()), new GiftEventMetadata(command.executedBy()));
        return giftAggregateRepository.save(giftAggregate);
    }

}
