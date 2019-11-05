package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import java.util.Objects;

public final class AggregateRootProjection {

    private final String aggregateRootId;

    private final String aggregateRootType;

    private final AggregateRoot aggregateRoot;

    private final Long version;

    public AggregateRootProjection(final AggregateRoot aggregateRoot) {
        this.aggregateRoot = Objects.requireNonNull(aggregateRoot);
        this.aggregateRootId = aggregateRoot.aggregateRootId();
        this.aggregateRootType = aggregateRoot.getClass().getSimpleName();
        this.version = aggregateRoot.version();
    }

    public String aggregateRootId() {
        return aggregateRootId;
    }

    public String aggregateRootType() {
        return aggregateRootType;
    }

    public AggregateRoot aggregateRoot() {
        return aggregateRoot;
    }

    public Long version() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AggregateRootProjection)) return false;
        AggregateRootProjection that = (AggregateRootProjection) o;
        return Objects.equals(aggregateRootId, that.aggregateRootId) &&
                Objects.equals(aggregateRootType, that.aggregateRootType) &&
                Objects.equals(aggregateRoot, that.aggregateRoot) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootId, aggregateRootType, aggregateRoot, version);
    }

    @Override
    public String toString() {
        return "AggregateRootProjection{" +
                "aggregateRootId='" + aggregateRootId + '\'' +
                ", aggregateRootType='" + aggregateRootType + '\'' +
                ", aggregateRoot=" + aggregateRoot +
                ", version=" + version +
                '}';
    }
}
