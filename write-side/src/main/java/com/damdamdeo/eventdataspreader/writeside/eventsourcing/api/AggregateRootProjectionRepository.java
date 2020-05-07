package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

public interface AggregateRootProjectionRepository {

    <T extends AggregateRoot> void merge(T aggregateRoot);

}
