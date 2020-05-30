package com.damdamdeo.eventsourced.consumer.api.eventsourcing;

public interface AggregateRootEventConsumer {

    void consume(AggregateRootEventConsumable aggregateRootEventConsumable);

    String aggregateRootType();

    String eventType();

}
