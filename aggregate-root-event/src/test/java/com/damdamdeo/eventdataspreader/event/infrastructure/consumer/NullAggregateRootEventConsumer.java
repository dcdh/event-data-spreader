package com.damdamdeo.eventdataspreader.event.infrastructure.consumer;

import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventConsumer;
import com.damdamdeo.eventdataspreader.event.api.consumer.AggregateRootEventConsumable;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NullAggregateRootEventConsumer implements AggregateRootEventConsumer {

    @Override
    public void consume(final AggregateRootEventConsumable aggregateRootEventConsumable) {
    }

    @Override
    public String aggregateRootType() {
        return null;
    }

    @Override
    public String eventType() {
        return null;
    }

}
