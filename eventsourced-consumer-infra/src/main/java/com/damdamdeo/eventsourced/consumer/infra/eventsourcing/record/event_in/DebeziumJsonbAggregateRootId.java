package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;

import java.util.Objects;

public final class DebeziumJsonbAggregateRootId implements AggregateRootId {

    private final String aggregateRootType;
    private final String aggregateRootId;

    public DebeziumJsonbAggregateRootId(final String aggregateRootType, final String aggregateRootId) {
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
    }

    @Override
    public String aggregateRootType() {
        return aggregateRootType;
    }

    @Override
    public String aggregateRootId() {
        return aggregateRootId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DebeziumJsonbAggregateRootId)) return false;
        DebeziumJsonbAggregateRootId that = (DebeziumJsonbAggregateRootId) o;
        return Objects.equals(aggregateRootType, that.aggregateRootType) &&
                Objects.equals(aggregateRootId, that.aggregateRootId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootType, aggregateRootId);
    }

    @Override
    public String toString() {
        return "DebeziumJsonbAggregateRootId{" +
                "aggregateRootType='" + aggregateRootType + '\'' +
                ", aggregateRootId='" + aggregateRootId + '\'' +
                '}';
    }
}
