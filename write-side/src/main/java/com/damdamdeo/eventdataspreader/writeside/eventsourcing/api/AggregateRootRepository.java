package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

public interface AggregateRootRepository {

    <T extends AggregateRoot> T save(T aggregateRoot);

    <T extends AggregateRoot> T load(String aggregateRootId, Class<T> clazz) throws UnknownAggregateRootException;

}
