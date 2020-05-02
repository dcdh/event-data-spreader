package com.damdamdeo.eventdataspreader.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public final class JdbcEncryptedEventSecret implements EncryptedEventSecret {

    private final String aggregateRootType;
    private final String aggregateRootId;
    private final String secret;

    public JdbcEncryptedEventSecret(final String aggregateRootType, final String aggregateRootId, final String secret) {
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.secret = Objects.requireNonNull(secret);
    }

    public JdbcEncryptedEventSecret(final ResultSet resultSet) throws SQLException {
        this(resultSet.getString("aggregateRootType"),
                resultSet.getString("aggregateRootId"),
                resultSet.getString("secret"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JdbcEncryptedEventSecret that = (JdbcEncryptedEventSecret) o;
        return Objects.equals(aggregateRootType, that.aggregateRootType) &&
                Objects.equals(aggregateRootId, that.aggregateRootId) &&
                Objects.equals(secret, that.secret);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootType, aggregateRootId, secret);
    }

    @Override
    public String aggregateRootType() {
        return aggregateRootType;
    }

    @Override
    public String aggregateRootId() {
        return aggregateRootId;
    }

    @Override
    public String secret() {
        return secret;
    }
}