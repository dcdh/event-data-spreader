package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.model.api.AggregateRootMaterializedState;
import com.damdamdeo.eventsourced.mutable.eventsourcing.GitCommitProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public final class PostgreSQLAggregateRootMaterializedState implements AggregateRootMaterializedState {

    private final AggregateRootId aggregateRootId;

    private final String serializedMaterializedState;

    private final Long version;

    public PreparedStatement upsertStatement(final Connection con, final GitCommitProvider gitCommitProvider) throws SQLException {
        final PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO AGGREGATE_ROOT_MATERIALIZED_STATE (aggregaterootid, aggregateroottype, serializedmaterializedstate, version, gitcommitid) " +
                "VALUES (?, ?, to_json(?::json), ?, ?) " +
                "ON CONFLICT ON CONSTRAINT aggregaterootmaterializedstate_pkey DO UPDATE SET serializedmaterializedstate = EXCLUDED.serializedmaterializedstate, version = EXCLUDED.version, gitcommitid = EXCLUDED.gitcommitid");
        preparedStatement.setString(1, aggregateRootId.aggregateRootId());
        preparedStatement.setString(2, aggregateRootId.aggregateRootType());
        preparedStatement.setString(3, serializedMaterializedState);
        preparedStatement.setLong(4, version);
        preparedStatement.setString(5, gitCommitProvider.gitCommitId());
        return preparedStatement;
    }

    public PostgreSQLAggregateRootMaterializedState(final AggregateRootId aggregateRootId,
                                                    final String serializedMaterializedState,
                                                    final Long version) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.serializedMaterializedState = Objects.requireNonNull(serializedMaterializedState);
        this.version = Objects.requireNonNull(version);
    }

    public PostgreSQLAggregateRootMaterializedState(final AggregateRootMaterializedState aggregateRootMaterializedState) {
        this(new PostgreSQLAggregateRootId(aggregateRootMaterializedState.aggregateRootId()),
                aggregateRootMaterializedState.serializedMaterializedState(),
                aggregateRootMaterializedState.version());
    }

    public PostgreSQLAggregateRootMaterializedState(final ResultSet resultSet) throws SQLException {
        this(new PostgreSQLAggregateRootId(resultSet),
                resultSet.getString("serializedmaterializedstate"),
                resultSet.getLong("version"));
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

    @Override
    public AggregateRootId aggregateRootId() {
        return aggregateRootId;
    }

    @Override
    public Long version() {
        return version;
    }

    @Override
    public String serializedMaterializedState() {
        return serializedMaterializedState;
    }
}
