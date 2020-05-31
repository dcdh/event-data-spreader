package com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization;

import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;

import java.util.Objects;

public final class DefaultAggregateRootEventId implements AggregateRootEventId {

    private final AggregateRootId aggregateRootId;
    private final Long version;

    public DefaultAggregateRootEventId(final AggregateRootId aggregateRootId, final Long version) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.version = Objects.requireNonNull(version);
    }

    @Override
    public AggregateRootId aggregateRootId() {
        return aggregateRootId;
    }

    @Override
    public Long version() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultAggregateRootEventId that = (DefaultAggregateRootEventId) o;
        return Objects.equals(aggregateRootId, that.aggregateRootId) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootId, version);
    }

    @Override
    public String toString() {
        return "DefaultAggregateRootEventId{" +
                "aggregateRootId=" + aggregateRootId +
                ", version=" + version +
                '}';
    }
}
