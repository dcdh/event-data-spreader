package com.damdamdeo.eventsourced.mutable.eventsourcing;

public interface AggregateRootEventPayload<T extends AggregateRoot> {

    void apply(T aggregateRoot);

}
