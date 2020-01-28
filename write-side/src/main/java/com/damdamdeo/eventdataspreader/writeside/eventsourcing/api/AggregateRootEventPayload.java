package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type")
public abstract class AggregateRootEventPayload<T extends AggregateRoot> {

    protected abstract void apply(T aggregateRoot);

    public abstract String eventName();

    public abstract String aggregateRootId();

    public abstract String aggregateRootType();

}
