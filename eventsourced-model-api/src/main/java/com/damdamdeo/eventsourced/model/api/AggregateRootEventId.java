package com.damdamdeo.eventsourced.model.api;

public interface AggregateRootEventId {

    AggregateRootId aggregateRootId();

    Long version();


}
