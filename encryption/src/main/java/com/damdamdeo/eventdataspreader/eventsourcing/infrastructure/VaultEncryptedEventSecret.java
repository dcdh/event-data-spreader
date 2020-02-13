package com.damdamdeo.eventdataspreader.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

import java.util.Objects;

public final class VaultEncryptedEventSecret implements EncryptedEventSecret {

    private final String aggregateRootId;
    private final String aggregateRootType;
    private final String secret;

    public VaultEncryptedEventSecret(final String aggregateRootId, final String aggregateRootType, final String secret) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
        this.secret = Objects.requireNonNull(secret);
    }

    @Override
    public String aggregateRootId() {
        return aggregateRootId;
    }

    @Override
    public String aggregateRootType() {
        return aggregateRootType;
    }

    @Override
    public String secret() {
        return secret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VaultEncryptedEventSecret)) return false;
        VaultEncryptedEventSecret that = (VaultEncryptedEventSecret) o;
        return Objects.equals(aggregateRootId, that.aggregateRootId) &&
                Objects.equals(aggregateRootType, that.aggregateRootType) &&
                Objects.equals(secret, that.secret);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootId, aggregateRootType, secret);
    }
}
