package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public final class PostgreSQLAggregateRootEventId implements AggregateRootEventId {

    private final AggregateRootId aggregateRootId;

    private final Long version;

    public PostgreSQLAggregateRootEventId(final ResultSet resultSet) throws SQLException {
        this.aggregateRootId = new PostgreSQLAggregateRootId(resultSet);
        this.version = Objects.requireNonNull(resultSet.getLong("version"));
    }

    public PostgreSQLAggregateRootEventId(final AggregateRootEventId aggregateRootEventId) {
        this.aggregateRootId = new PostgreSQLAggregateRootId(aggregateRootEventId);
        this.version = Objects.requireNonNull(aggregateRootEventId.version());
    }

    public String aggregateRootType() {
        return aggregateRootId.aggregateRootType();
    }

    @Override
    public AggregateRootId aggregateRootId() {
        return aggregateRootId;
    }

    @Override
    public Long version() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostgreSQLAggregateRootEventId that = (PostgreSQLAggregateRootEventId) o;
        return Objects.equals(aggregateRootId, that.aggregateRootId) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootId, version);
    }

    @Override
    public String toString() {
        return "PostgreSQLAggregateRootEventId{" +
                "aggregateRootId=" + aggregateRootId +
                ", version=" + version +
                '}';
    }
}
