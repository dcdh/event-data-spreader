package com.damdamdeo.eventdataspreader.writeside.eventconsumer;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.Event;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumer;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventQualifier;
import com.damdamdeo.eventdataspreader.writeside.aggregate.AccountAggregate;
import com.damdamdeo.eventdataspreader.writeside.aggregate.AccountAggregateRepository;
import com.damdamdeo.eventdataspreader.writeside.command.DebitAccountCommand;

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
        final String executedBy = event.metadata().getString("executedBy");
        final AccountAggregate accountAggregate = new AccountAggregate();
        accountAggregate.handle(new DebitAccountCommand(
                owner, price, executedBy));
        accountAggregateRepository.save(accountAggregate);
    }

}
