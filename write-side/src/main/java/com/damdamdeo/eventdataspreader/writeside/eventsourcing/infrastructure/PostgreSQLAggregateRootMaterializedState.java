package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.event.api.AggregateRootId;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootMaterializedState;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public final class PostgreSQLAggregateRootMaterializedState {

    private final AggregateRootId aggregateRootId;

    private final String serializedMaterializedState;

    private final Long version;

    public PreparedStatement upsertStatement(final Connection con) throws SQLException {
        final PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO AGGREGATE_ROOT_MATERIALIZED_STATE (aggregaterootid, aggregateroottype, serializedmaterializedstate, version) " +
                "VALUES (?, ?, to_json(?::json), ?) " +
                "ON CONFLICT ON CONSTRAINT aggregaterootmaterializedstate_pkey DO UPDATE SET serializedmaterializedstate = EXCLUDED.serializedmaterializedstate, version = EXCLUDED.version");
        preparedStatement.setString(1, aggregateRootId.aggregateRootId());
        preparedStatement.setString(2, aggregateRootId.aggregateRootType());
        preparedStatement.setString(3, serializedMaterializedState);
        preparedStatement.setLong(4, version);
        return preparedStatement;
    }

    public PostgreSQLAggregateRootMaterializedState(final AggregateRootMaterializedState aggregateRootMaterializedState) {
        this.aggregateRootId = new PostgreSQLAggregateRootId(aggregateRootMaterializedState.aggregateRootId(),
                aggregateRootMaterializedState.aggregateRootType());
        this.serializedMaterializedState = aggregateRootMaterializedState.serializedMaterializedState();
        this.version = aggregateRootMaterializedState.version();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostgreSQLAggregateRootMaterializedState that = (PostgreSQLAggregateRootMaterializedState) o;
        return Objects.equals(aggregateRootId, that.aggregateRootId) &&
                Objects.equals(serializedMaterializedState, that.serializedMaterializedState) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootId, serializedMaterializedState, version);
    }

    @Override
    public String toString() {
        return "PostgreSQLAggregateRootMaterializedState{" +
                "aggregateRootId=" + aggregateRootId +
                ", serializedMaterializedState='" + serializedMaterializedState + '\'' +
                ", version=" + version +
                '}';
    }
}
