package com.damdamdeo.eventsourced.mutable.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.model.api.AggregateRootMaterializedState;

import java.util.Objects;

public final class DefaultAggregateRootMaterializedState implements AggregateRootMaterializedState {

    private final AggregateRootId aggregateRootId;

    private final Long version;

    private final String serializedMaterializedState;

    public DefaultAggregateRootMaterializedState(final AggregateRootId aggregateRootId,
                                                 final Long version,
                                                 final String serializedMaterializedState) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.version = Objects.requireNonNull(version);
        this.serializedMaterializedState = Objects.requireNonNull(serializedMaterializedState);
    }

    public <T extends AggregateRoot> DefaultAggregateRootMaterializedState(final T aggregateRoot,
                                                                           final String serializedMaterializedState) {
        this(aggregateRoot.aggregateRootId(),
                aggregateRoot.version(),
                serializedMaterializedState);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultAggregateRootMaterializedState that = (DefaultAggregateRootMaterializedState) o;
        return Objects.equals(aggregateRootId, that.aggregateRootId) &&
                Objects.equals(version, that.version) &&
                Objects.equals(serializedMaterializedState, that.serializedMaterializedState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootId, version, serializedMaterializedState);
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
    public String serializedMaterializedState() {
        return serializedMaterializedState;
    }
}
