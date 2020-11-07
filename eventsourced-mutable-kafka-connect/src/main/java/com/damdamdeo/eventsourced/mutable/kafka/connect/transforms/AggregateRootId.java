package com.damdamdeo.eventsourced.mutable.kafka.connect.transforms;

import java.util.Objects;

public final class AggregateRootId {

    private final String aggregateRootType;
    private final String aggregateRootId;

    public AggregateRootId(final String aggregateRootType, final String aggregateRootId) {
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AggregateRootId)) return false;
        AggregateRootId that = (AggregateRootId) o;
        return Objects.equals(aggregateRootType, that.aggregateRootType) &&
                Objects.equals(aggregateRootId, that.aggregateRootId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootType, aggregateRootId);
    }

    @Override
    public String toString() {
        return "AggregateRootId{" +
                "aggregateRootType='" + aggregateRootType + '\'' +
                ", aggregateRootId='" + aggregateRootId + '\'' +
                '}';
    }
}
