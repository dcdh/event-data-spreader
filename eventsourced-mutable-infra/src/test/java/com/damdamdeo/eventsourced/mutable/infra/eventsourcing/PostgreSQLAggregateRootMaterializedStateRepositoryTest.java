package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootMaterializedState;
import com.damdamdeo.eventsourced.mutable.eventsourcing.ApiAggregateRootId;
import com.damdamdeo.eventsourced.mutable.eventsourcing.GitCommitProvider;
import com.damdamdeo.eventsourced.mutable.eventsourcing.UnknownAggregateRootException;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@QuarkusTest
public class PostgreSQLAggregateRootMaterializedStateRepositoryTest {

    @Inject
    @DataSource("mutable")
    AgroalDataSource mutableDataSource;

    @Inject
    PostgreSQLAggregateRootMaterializedStateRepository aggregateRootMaterializedStateRepository;

    @InjectMock
    GitCommitProvider gitCommitProvider;

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
        doReturn("3bc9898721c64c5d6d17724bf6ec1c715cca0f69").when(gitCommitProvider).gitCommitId();

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
            assertEquals("3bc9898721c64c5d6d17724bf6ec1c715cca0f69", resultSet.getString("gitcommitid"));
        }
        verify(gitCommitProvider, times(1)).gitCommitId();
    }

    @Test
    public void should_update_aggregate_root() throws SQLException {
        // Given
        doReturn("3bc9898721c64c5d6d17724bf6ec1c715cca0f69").when(gitCommitProvider).gitCommitId();

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
            assertEquals("3bc9898721c64c5d6d17724bf6ec1c715cca0f69", resultSet.getString("gitcommitid"));
        }
        verify(gitCommitProvider, times(2)).gitCommitId();
    }

    @Test
    public void should_throw_UnknownAggregateRootException_when_finding_unknown_materialized_state() {
        // Given

        // When
        final UnknownAggregateRootException unknownAggregateRootException = assertThrows(UnknownAggregateRootException.class,
                () -> aggregateRootMaterializedStateRepository.find(new ApiAggregateRootId("unknownAggregateRootId", "aggregateRootType")));

        // Then
        assertEquals(new UnknownAggregateRootException(new ApiAggregateRootId("unknownAggregateRootId", "aggregateRootType")),
                unknownAggregateRootException);
    }

    @Test
    public void should_return_expected_materialized_state_when_finding_existent_materialized_state() {
        // Given
        doReturn("3bc9898721c64c5d6d17724bf6ec1c715cca0f69").when(gitCommitProvider).gitCommitId();

        aggregateRootMaterializedStateRepository.persist(
                new PostgreSQLAggregateRootMaterializedState(
                        new PostgreSQLAggregateRootId("aggregateRootId", "aggregateRootType"), "{}", 0L));

        // When
        final AggregateRootMaterializedState aggregateRootMaterializedState = aggregateRootMaterializedStateRepository.find(
                new ApiAggregateRootId("aggregateRootId", "aggregateRootType"));

        // Then
        assertEquals(new PostgreSQLAggregateRootMaterializedState(new PostgreSQLAggregateRootId("aggregateRootId", "aggregateRootType"),
                        "{}", 0L),
                aggregateRootMaterializedState);
        verify(gitCommitProvider, atLeastOnce()).gitCommitId();
    }

}
