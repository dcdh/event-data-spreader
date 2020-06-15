package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootMaterializedState;
import com.damdamdeo.eventsourced.mutable.infra.resources.PostgreSQLTestResource;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(PostgreSQLTestResource.class)
public class PostgreSQLAggregateRootMaterializedStateRepositoryTest {

    @Inject
    @DataSource("mutable")
    AgroalDataSource mutableDataSource;

    @Inject
    PostgreSQLAggregateRootMaterializedStateRepository aggregateRootMaterializedStateRepository;

    @Test
    public void should_tables_be_initialised_at_application_startup() {
        Assertions.assertDoesNotThrow(() -> {
            try (final Connection con = mutableDataSource.getConnection();
                 final Statement stmt = con.createStatement();
                 final ResultSet rsSecretStore = stmt.executeQuery("SELECT * FROM AGGREGATE_ROOT_MATERIALIZED_STATE")) {
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @BeforeEach
    @AfterEach
    public void setup() {
        try (final Connection con = mutableDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE AGGREGATE_ROOT_MATERIALIZED_STATE");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void should_persist_new_aggregate_root() throws SQLException {
        // Given
        final AggregateRootMaterializedState aggregateRootMaterializedState
                = new PostgreSQLAggregateRootMaterializedState(new PostgreSQLAggregateRootId("aggregateRootId", "aggregateRootType"), "{}", 0L);

        // When
        aggregateRootMaterializedStateRepository.persist(aggregateRootMaterializedState);

        // Then
        try (final Connection con = mutableDataSource.getConnection();
             final Statement stmt = con.createStatement();
             final ResultSet resultSet = stmt.executeQuery("SELECT * FROM AGGREGATE_ROOT_MATERIALIZED_STATE")) {
            resultSet.next();
            assertEquals("aggregateRootId", resultSet.getString("aggregaterootid"));
            assertEquals("aggregateRootType", resultSet.getString("aggregateroottype"));
            assertEquals("{}", resultSet.getString("serializedmaterializedstate"));
            assertEquals(0, resultSet.getLong("version"));
        }
    }

    @Test
    public void should_update_aggregate_root() throws SQLException {
        // Given
        aggregateRootMaterializedStateRepository.persist(
                new PostgreSQLAggregateRootMaterializedState(
                        new PostgreSQLAggregateRootId("aggregateRootId", "aggregateRootType"), "{}", 0L));

        // When
        aggregateRootMaterializedStateRepository.persist(
                new PostgreSQLAggregateRootMaterializedState(
                        new PostgreSQLAggregateRootId("aggregateRootId", "aggregateRootType"), "{}", 1L));

        // Then
        try (final Connection con = mutableDataSource.getConnection();
             final Statement stmt = con.createStatement();
             final ResultSet resultSet = stmt.executeQuery("SELECT * FROM AGGREGATE_ROOT_MATERIALIZED_STATE")) {
            resultSet.next();
            assertEquals("aggregateRootId", resultSet.getString("aggregaterootid"));
            assertEquals("aggregateRootType", resultSet.getString("aggregateroottype"));
            assertEquals("{}", resultSet.getString("serializedmaterializedstate"));
            assertEquals(1, resultSet.getLong("version"));
        }
    }
}
