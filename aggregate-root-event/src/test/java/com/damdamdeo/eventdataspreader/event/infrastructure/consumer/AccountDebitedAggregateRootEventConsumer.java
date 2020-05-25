package com.damdamdeo.eventdataspreader.event.infrastructure.consumer;

import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventConsumer;
import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventConsumable;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AccountDebitedAggregateRootEventConsumer implements AggregateRootEventConsumer {

    @Override
    public void consume(final AggregateRootEventConsumable aggregateRootEventConsumable) {
        // Nothing to do
    }

    @Override
    public String aggregateRootType() {
        return "AccountAggregateRoot";
    }

    @Override
    public String eventType() {
        return "AccountDebited";
    }

}
