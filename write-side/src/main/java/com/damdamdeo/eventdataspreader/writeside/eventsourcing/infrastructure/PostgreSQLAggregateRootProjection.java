package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootSerializer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public final class PostgreSQLAggregateRootProjection {

    private final AggregateRootId aggregateRootId;

    private final String serializedAggregateRoot;

    private final Long version;

    public PreparedStatement upsertStatement(final Connection con) throws SQLException {
        final PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO AGGREGATE_ROOT_PROJECTION (aggregaterootid, aggregateroottype, serializedaggregateroot, version) " +
                "VALUES (?, ?, to_json(?::json), ?) " +
                "ON CONFLICT ON CONSTRAINT aggregaterootprojection_pkey DO UPDATE SET serializedaggregateroot = EXCLUDED.serializedaggregateroot, version = EXCLUDED.version");
        preparedStatement.setString(1, aggregateRootId.aggregateRootId);
        preparedStatement.setString(2, aggregateRootId.aggregateRootType);
        preparedStatement.setString(3, serializedAggregateRoot);
        preparedStatement.setLong(4, version);
        return preparedStatement;
    }

    public PostgreSQLAggregateRootProjection(final AggregateRoot aggregateRoot,
                                             final AggregateRootSerializer aggregateRootSerializer) {
        this.aggregateRootId = new AggregateRootId(aggregateRoot.aggregateRootId(),
                aggregateRoot.aggregateRootType());
        this.serializedAggregateRoot = aggregateRootSerializer.serialize(aggregateRoot);
        this.version = aggregateRoot.version();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostgreSQLAggregateRootProjection that = (PostgreSQLAggregateRootProjection) o;
        return Objects.equals(aggregateRootId, that.aggregateRootId) &&
                Objects.equals(serializedAggregateRoot, that.serializedAggregateRoot) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootId, serializedAggregateRoot, version);
    }

    @Override
    public String toString() {
        return "PostgreSQLAggregateRootProjection{" +
                "aggregateRootId=" + aggregateRootId +
                ", serializedAggregateRoot='" + serializedAggregateRoot + '\'' +
                ", version=" + version +
                '}';
    }
}
