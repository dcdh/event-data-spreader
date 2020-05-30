package com.damdamdeo.eventsourced.mutable.api.eventsourcing;

import java.util.Objects;

public final class UnknownAggregateRootException extends RuntimeException {

    private final String unknownAggregateId;

    public UnknownAggregateRootException(final String unknownAggregateId) {
        this.unknownAggregateId = Objects.requireNonNull(unknownAggregateId);
    }

    public String unknownAggregateId() {
        return unknownAggregateId;
    }

}
