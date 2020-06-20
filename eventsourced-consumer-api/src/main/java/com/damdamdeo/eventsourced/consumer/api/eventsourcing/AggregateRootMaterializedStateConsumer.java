package com.damdamdeo.eventsourced.consumer.api.eventsourcing;

import java.util.Objects;

// must be a class However it will be impossible to deserialize using Jackson
public abstract class AggregateRootMaterializedStateConsumer {

    private final String aggregateRootId;
    private final String aggregateRootType;
    private final Long version;

    public AggregateRootMaterializedStateConsumer(final String aggregateRootId,
                                                  final String aggregateRootType,
                                                  final Long version) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
        this.version = Objects.requireNonNull(version);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AggregateRootMaterializedStateConsumer that = (AggregateRootMaterializedStateConsumer) o;
        return Objects.equals(aggregateRootId, that.aggregateRootId) &&
                Objects.equals(aggregateRootType, that.aggregateRootType) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootId, aggregateRootType, version);
    }
}
