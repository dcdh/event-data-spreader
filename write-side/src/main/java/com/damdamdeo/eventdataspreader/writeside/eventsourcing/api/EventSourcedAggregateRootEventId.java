package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import com.damdamdeo.eventdataspreader.event.api.AggregateRootEventId;
import com.damdamdeo.eventdataspreader.event.api.AggregateRootId;

import java.util.Objects;

public final class EventSourcedAggregateRootEventId implements AggregateRootEventId {

    private final AggregateRootId evenSourcedAggregateRootId;
    private final Long version;

    public EventSourcedAggregateRootEventId(final String aggregateRootId, final String aggregateRootType, final Long version) {
        this.evenSourcedAggregateRootId = Objects.requireNonNull(new EvenSourcedAggregateRootId(aggregateRootId, aggregateRootType));
        this.version = Objects.requireNonNull(version);
    }

    @Override
    public AggregateRootId aggregateRootId() {
        return evenSourcedAggregateRootId;
    }

    @Override
    public Long version() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventSourcedAggregateRootEventId that = (EventSourcedAggregateRootEventId) o;
        return Objects.equals(evenSourcedAggregateRootId, that.evenSourcedAggregateRootId) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(evenSourcedAggregateRootId, version);
    }

    @Override
    public String toString() {
        return "EventSourcedAggregateRootEventId{" +
                "evenSourcedAggregateRootId=" + evenSourcedAggregateRootId +
                ", version=" + version +
                '}';
    }
}
