package com.damdamdeo.eventdataspreader.writeside.consumer;

import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventConsumable;
import com.damdamdeo.eventdataspreader.event.api.AggregateRootEventId;
import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventConsumer;
import com.damdamdeo.eventdataspreader.writeside.aggregate.AccountAggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.consumer.metadata.UserAggregateRootEventMetadataConsumer;
import com.damdamdeo.eventdataspreader.writeside.consumer.payload.GiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer;
import com.damdamdeo.eventdataspreader.writeside.command.DebitAccountCommand;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootRepository;

import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.util.Objects;

@ApplicationScoped
public class DebitAccountFollowingGiftBoughtAggregateRootEventConsumer implements AggregateRootEventConsumer {

    final AggregateRootRepository aggregateRootRepository;
    final AccountAggregateRootProvider accountAggregateRootProvider;

    public DebitAccountFollowingGiftBoughtAggregateRootEventConsumer(final AggregateRootRepository aggregateRootRepository,
                                                                     final AccountAggregateRootProvider accountAggregateRootProvider) {
        this.aggregateRootRepository = Objects.requireNonNull(aggregateRootRepository);
        this.accountAggregateRootProvider = Objects.requireNonNull(accountAggregateRootProvider);
    }

    // je creer une commande à partir d'un event ;)
    @Override
    public void consume(final AggregateRootEventConsumable aggregateRootEventConsumable) {
        final GiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer giftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer = (GiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer) aggregateRootEventConsumable.eventPayload();
        final UserAggregateRootEventMetadataConsumer eventMetadata = (UserAggregateRootEventMetadataConsumer) aggregateRootEventConsumable.eventMetaData();
        final AggregateRootEventId aggregateRootEventId = aggregateRootEventConsumable.eventId(); // a stocker dans le metadata !
        final BigDecimal price = new BigDecimal("100");
        final String owner = eventMetadata.executedBy();
        final String executedBy = eventMetadata.executedBy();
        final AccountAggregateRoot accountAggregateRoot = this.accountAggregateRootProvider.create();
        accountAggregateRoot.handle(new DebitAccountCommand(
                owner, price, executedBy));
        aggregateRootRepository.save(accountAggregateRoot);
    }

    @Override
    public String aggregateRootType() {
        return "GiftAggregateRoot";
    }

    @Override
    public String eventType() {
        return "GiftAggregateRootGiftBoughtAggregateRootEventPayload";
    }

}
