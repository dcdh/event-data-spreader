package com.damdamdeo.eventsourced.encryption.api;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;

import java.util.Objects;

public final class UnableToDecryptMissingSecretException extends RuntimeException {

    private final AggregateRootId aggregateRootId;

    public UnableToDecryptMissingSecretException(final AggregateRootId aggregateRootId) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
    }

    public AggregateRootId aggregateRootId() {
        return aggregateRootId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnableToDecryptMissingSecretException)) return false;
        UnableToDecryptMissingSecretException that = (UnableToDecryptMissingSecretException) o;
        return Objects.equals(aggregateRootId, that.aggregateRootId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootId);
    }
}
