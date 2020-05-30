package com.damdamdeo.eventsourced.model.api;

public interface AggregateRootMaterializedState {

    AggregateRootId aggregateRootId();

    Long version();

    String serializedMaterializedState();

}
