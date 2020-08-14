package com.damdamdeo.eventsourced.mutable.api.eventsourcing;

public interface AggregateRootEventPayload<T extends AggregateRoot> {

    void apply(T aggregateRoot);

}
