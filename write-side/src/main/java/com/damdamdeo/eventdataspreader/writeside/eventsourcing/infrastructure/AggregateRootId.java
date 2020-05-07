package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import java.util.Objects;

public final class AggregateRootId {

    final String aggregateRootId;

    final String aggregateRootType;

    public AggregateRootId(final String aggregateRootId,
                           final String aggregateRootType) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AggregateRootId that = (AggregateRootId) o;
        return Objects.equals(aggregateRootId, that.aggregateRootId) &&
                Objects.equals(aggregateRootType, that.aggregateRootType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootId, aggregateRootType);
    }
}
