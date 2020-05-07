package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootProjectionRepository;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootSerializer;
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
public class AgroalDataSourcePostgreSqlAggregateRootProjectionRepository implements AggregateRootProjectionRepository {

    private static final String POSTGRESQL_DDL_FILE = "/sql/aggregate-root-projection-postgresql.ddl";

    private final AggregateRootSerializer aggregateRootSerializer;
    private final AgroalDataSource aggregateRootProjectionEventStoreDataSource;

    public AgroalDataSourcePostgreSqlAggregateRootProjectionRepository(@DataSource("aggregate-root-projection-event-store") final AgroalDataSource aggregateRootProjectionEventStoreDataSource,
            final AggregateRootSerializer aggregateRootSerializer) {
        this.aggregateRootSerializer = Objects.requireNonNull(aggregateRootSerializer);
        this.aggregateRootProjectionEventStoreDataSource = Objects.requireNonNull(aggregateRootProjectionEventStoreDataSource);
    }

    @PostConstruct
    public void initTables() {
        final InputStream ddlResource = this.getClass().getResourceAsStream(POSTGRESQL_DDL_FILE);
        try (final Scanner scanner = new Scanner(ddlResource).useDelimiter("!!");
             final Connection con = aggregateRootProjectionEventStoreDataSource.getConnection();
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
    public <T extends AggregateRoot> void merge(T aggregateRoot) {
        final PostgreSQLAggregateRootProjection postgreSQLAggregateRootProjection = new PostgreSQLAggregateRootProjection(aggregateRoot, aggregateRootSerializer);
        try (final Connection connection = aggregateRootProjectionEventStoreDataSource.getConnection();
             final PreparedStatement preparedStatement = postgreSQLAggregateRootProjection.upsertStatement(connection)) {
            preparedStatement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
