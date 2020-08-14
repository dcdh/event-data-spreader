package com.damdamdeo.eventsourced.mutable.api.eventsourcing;

import java.util.Objects;

public final class UnsupportedAggregateRootEventPayload extends RuntimeException {

    private final String aggregateRootType;
    private final String eventType;

    public UnsupportedAggregateRootEventPayload(final String aggregateRootType, final String eventType) {
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
        this.eventType = Objects.requireNonNull(eventType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnsupportedAggregateRootEventPayload)) return false;
        UnsupportedAggregateRootEventPayload that = (UnsupportedAggregateRootEventPayload) o;
        return Objects.equals(aggregateRootType, that.aggregateRootType) &&
                Objects.equals(eventType, that.eventType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootType, eventType);
    }

    @Override
    public String toString() {
        return "UnsupportedAggregateRootEventPayload{" +
                "aggregateRootType='" + aggregateRootType + '\'' +
                ", eventType='" + eventType + '\'' +
                "} " + super.toString();
    }
}
