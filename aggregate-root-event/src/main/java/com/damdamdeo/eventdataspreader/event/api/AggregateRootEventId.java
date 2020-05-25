package com.damdamdeo.eventdataspreader.event.api;

public interface AggregateRootEventId {

    AggregateRootId aggregateRootId();

    Long version();


}
