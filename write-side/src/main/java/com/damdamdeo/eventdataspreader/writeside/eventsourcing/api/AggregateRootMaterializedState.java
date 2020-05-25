package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import java.util.Objects;

public final class AggregateRootMaterializedState {

    private final String aggregateRootId;

    private final String aggregateRootType;

    private final Long version;

    private final String serializedMaterializedState;

    public AggregateRootMaterializedState(final String aggregateRootId,
                                          final String aggregateRootType,
                                          final Long version,
                                          final String serializedMaterializedState) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
        this.version = Objects.requireNonNull(version);
        this.serializedMaterializedState = Objects.requireNonNull(serializedMaterializedState);
    }

    public <T extends AggregateRoot> AggregateRootMaterializedState(final T aggregateRoot,
                                                                    final String serializedMaterializedState) {
        this(aggregateRoot.aggregateRootId(),
                aggregateRoot.aggregateRootType(),
                aggregateRoot.version(),
                serializedMaterializedState);
    }

    public String aggregateRootId() {
        return aggregateRootId;
    }

    public String aggregateRootType() {
        return aggregateRootType;
    }

    public Long version() {
        return version;
    }

    public String serializedMaterializedState() {
        return serializedMaterializedState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AggregateRootMaterializedState that = (AggregateRootMaterializedState) o;
        return Objects.equals(aggregateRootId, that.aggregateRootId) &&
                Objects.equals(aggregateRootType, that.aggregateRootType) &&
                Objects.equals(version, that.version) &&
                Objects.equals(serializedMaterializedState, that.serializedMaterializedState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootId, aggregateRootType, version, serializedMaterializedState);
    }
}
