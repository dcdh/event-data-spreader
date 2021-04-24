package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.model.api.AggregateRootMaterializedState;
import com.damdamdeo.eventsourced.mutable.eventsourcing.AggregateRootMaterializedStateRepository;
import com.damdamdeo.eventsourced.mutable.eventsourcing.GitCommitProvider;
import com.damdamdeo.eventsourced.mutable.eventsourcing.UnknownAggregateRootException;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.runtime.StartupEvent;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.io.InputStream;
import java.sql.*;
import java.time.Duration;
import java.util.Objects;
import java.util.Scanner;

@ApplicationScoped
public class PostgreSQLAggregateRootMaterializedStateRepository implements AggregateRootMaterializedStateRepository {

    private static final String POSTGRESQL_DDL_FILE = "/sql/aggregate-root-materialized-state-postgresql.ddl";

    private final AgroalDataSource mutableDataSource;
    private final GitCommitProvider gitCommitProvider;

    public PostgreSQLAggregateRootMaterializedStateRepository(@DataSource("mutable") final AgroalDataSource mutableDataSource,
                                                              final GitCommitProvider gitCommitProvider) {
        this.mutableDataSource = Objects.requireNonNull(mutableDataSource);
        this.gitCommitProvider = Objects.requireNonNull(gitCommitProvider);
    }

    public void onStart(@Observes final StartupEvent ev) {
        final RetryPolicy<Object> retryPolicy = new RetryPolicy<>().handle(Exception.class)
                .withDelay(Duration.ofMillis(100))
                .withMaxRetries(100);
        final InputStream ddlResource = this.getClass().getResourceAsStream(POSTGRESQL_DDL_FILE);
        try (final Scanner scanner = new Scanner(ddlResource).useDelimiter("!!");
             final Connection con = mutableDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            while (scanner.hasNext()) {
                final String ddlEntry = scanner.next().trim();
                if (!ddlEntry.isEmpty()) {
                    Failsafe.with(retryPolicy).run(() -> stmt.executeUpdate(ddlEntry));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void persist(final AggregateRootMaterializedState aggregateRootMaterializedState) {
        final PostgreSQLAggregateRootMaterializedState postgreSQLAggregateRootMaterializedState = new PostgreSQLAggregateRootMaterializedState(aggregateRootMaterializedState);
        try (final Connection connection = mutableDataSource.getConnection();
             final PreparedStatement preparedStatement = postgreSQLAggregateRootMaterializedState.upsertStatement(connection, gitCommitProvider)) {
            preparedStatement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AggregateRootMaterializedState find(AggregateRootId aggregateRootId) {
        try (final Connection connection = mutableDataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT aggregaterootid, aggregateroottype, serializedmaterializedstate, version FROM AGGREGATE_ROOT_MATERIALIZED_STATE WHERE aggregaterootid = ? AND aggregateroottype = ?")) {
            preparedStatement.setString(1, aggregateRootId.aggregateRootId());
            preparedStatement.setString(2, aggregateRootId.aggregateRootType());
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new PostgreSQLAggregateRootMaterializedState(resultSet);
            }
            throw new UnknownAggregateRootException(aggregateRootId);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
