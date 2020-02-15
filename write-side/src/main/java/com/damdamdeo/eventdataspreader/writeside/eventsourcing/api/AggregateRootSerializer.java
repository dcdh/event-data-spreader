package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

public interface AggregateRootSerializer {

    String serialize(AggregateRoot aggregateRoot);

}
