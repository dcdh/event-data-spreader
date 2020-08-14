package com.damdamdeo.eventsourced.encryption.api;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;

import java.util.Objects;

public final class UnableToEncryptMissingSecretException extends RuntimeException {

    private final AggregateRootId aggregateRootId;

    public UnableToEncryptMissingSecretException(final AggregateRootId aggregateRootId) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
    }

    public AggregateRootId aggregateRootId() {
        return aggregateRootId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnableToEncryptMissingSecretException that = (UnableToEncryptMissingSecretException) o;
        return Objects.equals(aggregateRootId, that.aggregateRootId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootId);
    }
}
