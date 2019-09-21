package com.damdamdeo.eventdataspreader.writeside.eventconsumer;

import com.damdamdeo.eventdataspreader.writeside.aggregate.AccountAggregate;
import com.damdamdeo.eventdataspreader.writeside.aggregate.AccountAggregateRepository;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.AccountDebited;
import com.damdamdeo.eventdataspreader.writeside.aggregate.event.DefaultEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.debeziumeventconsumer.api.Event;
import com.damdamdeo.eventdataspreader.writeside.debeziumeventconsumer.api.EventConsumer;
import com.damdamdeo.eventdataspreader.writeside.debeziumeventconsumer.api.EventQualifier;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;

@ApplicationScoped
@EventQualifier(aggregateRootType = "GiftAggregate", eventType = "GiftBought")
public class DebitAccountFollowingGiftBoughtEventConsumer implements EventConsumer {

    @Inject
    AccountAggregateRepository accountAggregateRepository;

    // je creer une commande à partir d'un event ;)
    @Override
    public void consume(final Event event) {
        final String owner = event.metadata().getString("executedBy");
        final BigDecimal price = new BigDecimal("100");
        final AccountAggregate accountAggregate = new AccountAggregate();
        accountAggregate.apply(new AccountDebited(owner, price), new DefaultEventMetadata(owner));
        accountAggregateRepository.save(accountAggregate);
    }

}
