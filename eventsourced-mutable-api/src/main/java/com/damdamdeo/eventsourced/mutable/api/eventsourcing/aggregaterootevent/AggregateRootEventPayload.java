package com.damdamdeo.eventsourced.mutable.api.eventsourcing.aggregaterootevent;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;

// must be a class However it will be impossible to deserialize using Jackson
public abstract class AggregateRootEventPayload<T extends AggregateRoot> {

    public abstract void apply(T aggregateRoot);

    public abstract String eventPayloadName();

    public abstract AggregateRootId aggregateRootId();

}
