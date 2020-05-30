package com.damdamdeo.eventsourced.encryption.infra;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public final class JdbcAggregateRootId implements AggregateRootId {

    private final String aggregateRootId;
    private final String aggregateRootType;

    public JdbcAggregateRootId(final String aggregateRootId, final String aggregateRootType) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
    }

    public JdbcAggregateRootId(final ResultSet resultSet) throws SQLException {
        this(resultSet.getString("aggregateRootType"),
                resultSet.getString("aggregateRootId"));
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JdbcAggregateRootId that = (JdbcAggregateRootId) o;
        return Objects.equals(aggregateRootId, that.aggregateRootId) &&
                Objects.equals(aggregateRootType, that.aggregateRootType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootId, aggregateRootType);
    }

    @Override
    public String toString() {
        return "JdbcAggregateRootId{" +
                "aggregateRootId='" + aggregateRootId + '\'' +
                ", aggregateRootType='" + aggregateRootType + '\'' +
                '}';
    }
}
