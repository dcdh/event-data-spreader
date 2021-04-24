package com.damdamdeo.eventsourced.mutable.eventsourcing;

import java.util.Objects;

public final class UnsupportedAggregateRoot extends RuntimeException {

    private final String aggregateRootType;

    public UnsupportedAggregateRoot(final String aggregateRootType) {
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnsupportedAggregateRoot)) return false;
        UnsupportedAggregateRoot that = (UnsupportedAggregateRoot) o;
        return Objects.equals(aggregateRootType, that.aggregateRootType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootType);
    }

    @Override
    public String toString() {
        return "UnsupportedAggregateRoot{" +
                "aggregateRootType='" + aggregateRootType + '\'' +
                "} " + super.toString();
    }
}
