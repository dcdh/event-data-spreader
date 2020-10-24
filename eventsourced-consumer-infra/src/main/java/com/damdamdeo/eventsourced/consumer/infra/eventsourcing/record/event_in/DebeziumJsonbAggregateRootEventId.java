package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in;

import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;

import java.util.Objects;

public final class DebeziumJsonbAggregateRootEventId implements AggregateRootEventId {

    private final DebeziumJsonbAggregateRootId aggregateRootId;

    private final Long version;

    public DebeziumJsonbAggregateRootEventId(final DebeziumJsonbAggregateRootId debeziumJsonbAggregateRootId, final Long version) {
        this.aggregateRootId = Objects.requireNonNull(debeziumJsonbAggregateRootId);
        this.version = Objects.requireNonNull(version);
    }

    public DebeziumJsonbAggregateRootEventId(final DebeziumJsonbAggregateRootEvent debeziumJsonbAggregateRootEvent) {
        this(new DebeziumJsonbAggregateRootId(debeziumJsonbAggregateRootEvent.aggregateRootType(), debeziumJsonbAggregateRootEvent.aggregateRootId()),
                debeziumJsonbAggregateRootEvent.version());
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
        if (!(o instanceof DebeziumJsonbAggregateRootEventId)) return false;
        DebeziumJsonbAggregateRootEventId that = (DebeziumJsonbAggregateRootEventId) o;
        return Objects.equals(aggregateRootId, that.aggregateRootId) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootId, version);
    }

    @Override
    public String toString() {
        return "DebeziumJsonbAggregateRootEventId{" +
                "aggregateRootId=" + aggregateRootId +
                ", version=" + version +
                '}';
    }
}
