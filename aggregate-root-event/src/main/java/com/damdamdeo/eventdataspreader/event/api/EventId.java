package com.damdamdeo.eventdataspreader.event.api;

public interface EventId {

    String aggregateRootId();

    String aggregateRootType();

    Long version();

}
