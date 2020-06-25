package com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;

public final class UnsupportedAggregateRootEventPayload extends AggregateRootEventPayload {

    @Override
    public void apply(final AggregateRoot aggregateRoot) {
        throw new UnsupportedOperationException("Unsupported aggregate root type");
    }

    @Override
    public String eventPayloadName() {
        throw new UnsupportedOperationException("Unsupported aggregate root type");
    }

    @Override
    public AggregateRootId aggregateRootId() {
        throw new UnsupportedOperationException("Unsupported aggregate root type");
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return true;
    }

}
