package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public final class PostgreSQLAggregateRootId implements AggregateRootId {

    private final String aggregateRootId;
    private final String aggregateRootType;

    public PostgreSQLAggregateRootId(final String aggregateRootId, final String aggregateRootType) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
    }

    public PostgreSQLAggregateRootId(final AggregateRootId aggregateRootId) {
        this(aggregateRootId.aggregateRootId(), aggregateRootId.aggregateRootType());
    }

    public PostgreSQLAggregateRootId(final ResultSet resultSet) throws SQLException  {
        this(Objects.requireNonNull(resultSet.getString("aggregaterootid")),
                Objects.requireNonNull(resultSet.getString("aggregateroottype")));
    }

    public PostgreSQLAggregateRootId(final AggregateRootEventId aggregateRootEventId) {
        this(aggregateRootEventId.aggregateRootId());
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
        PostgreSQLAggregateRootId that = (PostgreSQLAggregateRootId) o;
        return Objects.equals(aggregateRootId, that.aggregateRootId) &&
                Objects.equals(aggregateRootType, that.aggregateRootType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootId, aggregateRootType);
    }

    @Override
    public String toString() {
        return "PostgreSQLAggregateRootId{" +
                "aggregateRootId='" + aggregateRootId + '\'' +
                ", aggregateRootType='" + aggregateRootType + '\'' +
                '}';
    }
}
