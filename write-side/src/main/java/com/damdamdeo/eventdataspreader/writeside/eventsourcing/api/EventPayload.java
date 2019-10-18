package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

public abstract class EventPayload<T extends AggregateRoot> {

    protected abstract void apply(T aggregateRoot);

    public abstract EventPayloadIdentifier eventPayloadIdentifier();

}
