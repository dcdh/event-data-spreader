package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

public interface AggregateRootProjectionRepository {

    AggregateRootProjection save(AggregateRootProjection aggregateRootProjection);

}
