package com.damdamdeo.eventsourced.mutable.api.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;

import java.util.Objects;

public final class UnknownAggregateRootException extends RuntimeException {

    private final AggregateRootId unknownAggregateId;

    public UnknownAggregateRootException(final AggregateRootId unknownAggregateId) {
        this.unknownAggregateId = Objects.requireNonNull(unknownAggregateId);
    }

    public AggregateRootId unknownAggregateId() {
        return unknownAggregateId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnknownAggregateRootException)) return false;
        UnknownAggregateRootException that = (UnknownAggregateRootException) o;
        return Objects.equals(unknownAggregateId, that.unknownAggregateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unknownAggregateId);
    }

    @Override
    public String toString() {
        return "UnknownAggregateRootException{" +
                "unknownAggregateId=" + unknownAggregateId +
                "} " + super.toString();
    }
}
