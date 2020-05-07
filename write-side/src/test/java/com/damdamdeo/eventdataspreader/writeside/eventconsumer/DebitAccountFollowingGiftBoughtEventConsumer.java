package com.damdamdeo.eventdataspreader.writeside.eventconsumer;

import com.damdamdeo.eventdataspreader.event.api.Event;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumer;
import com.damdamdeo.eventdataspreader.writeside.aggregate.AccountAggregate;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.DefaultEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.command.DebitAccountCommand;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootRepository;
import com.damdamdeo.eventdataspreader.writeside.query.event.GiftAggregateGiftBoughtEventPayload;

import javax.enterprise.context.Dependent;
import java.math.BigDecimal;
import java.util.Objects;

@Dependent
public class DebitAccountFollowingGiftBoughtEventConsumer implements EventConsumer {

    final AggregateRootRepository aggregateRootRepository;

    public DebitAccountFollowingGiftBoughtEventConsumer(final AggregateRootRepository aggregateRootRepository) {
        this.aggregateRootRepository = Objects.requireNonNull(aggregateRootRepository);
    }

    // je creer une commande à partir d'un event ;)
    @Override
    public void consume(final Event event) {
        final GiftAggregateGiftBoughtEventPayload giftAggregateGiftBoughtEventPayload = (GiftAggregateGiftBoughtEventPayload) event.eventPayload();
        final DefaultEventMetadata eventMetadata = (DefaultEventMetadata) event.eventMetaData();
        final Long version = event.version();
        final BigDecimal price = new BigDecimal("100");
        final String owner = eventMetadata.executedBy();
        final String executedBy = eventMetadata.executedBy();
        final AccountAggregate accountAggregate = new AccountAggregate();
        accountAggregate.handle(new DebitAccountCommand(
                owner, price, executedBy));
        aggregateRootRepository.save(accountAggregate);
    }

    @Override
    public String aggregateRootType() {
        return "GiftAggregate";
    }

    @Override
    public String eventType() {
        return "GiftBought";
    }

}
