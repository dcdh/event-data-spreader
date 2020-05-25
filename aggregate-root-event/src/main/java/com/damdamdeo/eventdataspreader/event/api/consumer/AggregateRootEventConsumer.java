package com.damdamdeo.eventdataspreader.event.api.consumer;

public interface AggregateRootEventConsumer {

    void consume(AggregateRootEventConsumable aggregateRootEventConsumable);

    String aggregateRootType();

    String eventType();

}
