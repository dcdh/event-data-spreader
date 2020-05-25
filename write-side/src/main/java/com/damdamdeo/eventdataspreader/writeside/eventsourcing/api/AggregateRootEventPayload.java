package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

// must be a class However it will be impossible to deserialize using Jackson
public abstract class AggregateRootEventPayload<T extends AggregateRoot> {

    public abstract void apply(T aggregateRoot);

    public abstract String eventPayloadName();

    public abstract String aggregateRootId();

    public abstract String aggregateRootType();

}
