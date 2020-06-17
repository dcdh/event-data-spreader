package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootMaterializedState;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRootMaterializedStateRepository;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.GitCommitProvider;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.runtime.Startup;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.Scanner;

@Startup
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

    @PostConstruct
    public void initTables() {
        final InputStream ddlResource = this.getClass().getResourceAsStream(POSTGRESQL_DDL_FILE);
        try (final Scanner scanner = new Scanner(ddlResource).useDelimiter("!!");
             final Connection con = mutableDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            while (scanner.hasNext()) {
                final String ddlEntry = scanner.next().trim();
                if (!ddlEntry.isEmpty()) {
                    stmt.executeUpdate(ddlEntry);
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

}
