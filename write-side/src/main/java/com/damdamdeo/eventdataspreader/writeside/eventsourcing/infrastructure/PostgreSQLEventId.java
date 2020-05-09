package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.event.api.EventId;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public final class PostgreSQLEventId implements EventId {

    private final String aggregateRootId;
    private final String aggregateRootType;
    private final Long version;

    public PostgreSQLEventId(final ResultSet resultSet) throws SQLException {
        this.aggregateRootId = Objects.requireNonNull(resultSet.getString("aggregaterootid"));
        this.aggregateRootType = Objects.requireNonNull(resultSet.getString("aggregateroottype"));
        this.version = Objects.requireNonNull(resultSet.getLong("version"));
    }

    public PostgreSQLEventId(final EventId eventId) {
        this.aggregateRootId = Objects.requireNonNull(eventId.aggregateRootId());
        this.aggregateRootType = Objects.requireNonNull(eventId.aggregateRootType());
        this.version = Objects.requireNonNull(eventId.version());
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
    public Long version() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostgreSQLEventId that = (PostgreSQLEventId) o;
        return Objects.equals(aggregateRootId, that.aggregateRootId) &&
                Objects.equals(aggregateRootType, that.aggregateRootType) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootId, aggregateRootType, version);
    }

    @Override
    public String toString() {
        return "PostgreSQLEventId{" +
                "aggregateRootId='" + aggregateRootId + '\'' +
                ", aggregateRootType='" + aggregateRootType + '\'' +
                ", version=" + version +
                '}';
    }
}
