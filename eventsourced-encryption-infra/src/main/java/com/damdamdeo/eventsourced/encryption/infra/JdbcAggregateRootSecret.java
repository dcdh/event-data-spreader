package com.damdamdeo.eventsourced.encryption.infra;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public final class JdbcAggregateRootSecret implements AggregateRootSecret {

    private final AggregateRootId aggregateRootId;
    private final String secret;

    public JdbcAggregateRootSecret(final String aggregateRootType, final String aggregateRootId, final String secret) {
        this.aggregateRootId = new JdbcAggregateRootId(aggregateRootId, aggregateRootType);
        this.secret = Objects.requireNonNull(secret);
    }

    public JdbcAggregateRootSecret(final ResultSet resultSet) throws SQLException {
        this(resultSet.getString("aggregateRootType"),
                resultSet.getString("aggregateRootId"),
                resultSet.getString("secret"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JdbcAggregateRootSecret that = (JdbcAggregateRootSecret) o;
        return Objects.equals(aggregateRootId, that.aggregateRootId) &&
                Objects.equals(secret, that.secret);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootId, secret);
    }

    @Override
    public AggregateRootId aggregateRootId() {
        return aggregateRootId;
    }

    @Override
    public String secret() {
        return secret;
    }
}