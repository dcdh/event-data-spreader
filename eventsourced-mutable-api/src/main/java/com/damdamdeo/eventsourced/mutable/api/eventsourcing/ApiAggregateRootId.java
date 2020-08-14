package com.damdamdeo.eventsourced.mutable.api.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;

import java.util.Objects;

public final class ApiAggregateRootId implements AggregateRootId {

    private final String aggregateRootId;
    private final String aggregateRootType;

    public ApiAggregateRootId(final String aggregateRootId,
                              final String aggregateRootType) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
    }

    @Override
    public String aggregateRootId() {
        return aggregateRootId;
    }

    @Override
    public String aggregateRootType() {
        return aggregateRootType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiAggregateRootId that = (ApiAggregateRootId) o;
        return Objects.equals(aggregateRootId, that.aggregateRootId) &&
                Objects.equals(aggregateRootType, that.aggregateRootType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootId, aggregateRootType);
    }

    @Override
    public String toString() {
        return "ApiAggregateRootId{" +
                "aggregateRootId='" + aggregateRootId + '\'' +
                ", aggregateRootType='" + aggregateRootType + '\'' +
                '}';
    }
}
