package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

public interface AggregateRootMaterializedStateRepository {

    void persist(AggregateRootMaterializedState aggregateRootMaterializedState);

}
