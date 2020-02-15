package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

public interface EventId {

    String aggregateRootId();

    String aggregateRootType();

    Long version();

}
