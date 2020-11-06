package com.damdamdeo.eventsourced.consumer.api.eventsourcing;


public interface AggregateRootEventConsumer<INFRA> {

    void consume(AggregateRootEventConsumable<INFRA> aggregateRootEventConsumable, Operation operation);

    String aggregateRootType();

    String eventType();

}
