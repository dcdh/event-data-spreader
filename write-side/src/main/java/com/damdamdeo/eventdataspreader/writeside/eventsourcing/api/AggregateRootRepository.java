package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

public interface AggregateRootRepository<T extends AggregateRoot> {

    T save(T aggregateRoot);

    T load(String aggregateRootId) throws UnknownAggregateRootException;

}
