package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.event.api.EventMetadata;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootProjectionRepository;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.test.junit.QuarkusTest;
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
public class AgroalDataSourcePostgreSqlAggregateRootProjectionRepositoryTest {

    @Inject
    @DataSource("aggregate-root-projection-event-store")
    AgroalDataSource aggregateRootProjectionEventStoreDataSource;

    @Inject
    AggregateRootProjectionRepository aggregateRootProjectionRepository;

    @Test
    public void should_tables_be_initialised_at_application_startup() {
        Assertions.assertDoesNotThrow(() -> {
            try (final Connection con = aggregateRootProjectionEventStoreDataSource.getConnection();
                 final Statement stmt = con.createStatement();
                 final ResultSet rsSecretStore = stmt.executeQuery("SELECT * FROM AGGREGATE_ROOT_PROJECTION")) {
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @BeforeEach
    public void setup() {
        try (final Connection con = aggregateRootProjectionEventStoreDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE AGGREGATE_ROOT_PROJECTION");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static class TestAggregateRoot extends AggregateRoot {

        public TestAggregateRoot() {}

        public void handle() {
            apply(new TestAggregateRootEventPayload(), new TestEventMetadata());
        }

    }

    private static class TestAggregateRootEventPayload extends AggregateRootEventPayload<TestAggregateRoot> {

        @Override
        protected void apply(final TestAggregateRoot aggregateRoot) {

        }

        @Override
        public String eventName() {
            return "TestAggregateRootEvent";
        }

        @Override
        public String aggregateRootId() {
            return "aggregateRootId";
        }

        @Override
        public String aggregateRootType() {
            return "aggregateRootType";
        }
    }

    private static class TestEventMetadata implements EventMetadata {

    }

    @Test
    public void should_persist_new_aggregate_root() throws SQLException {
        // Given
        final TestAggregateRoot testAggregateRoot = new TestAggregateRoot();
        testAggregateRoot.handle();

        // When
        aggregateRootProjectionRepository.merge(testAggregateRoot);

        // Then
        try (final Connection con = aggregateRootProjectionEventStoreDataSource.getConnection();
             final Statement stmt = con.createStatement();
             final ResultSet resultSet = stmt.executeQuery("SELECT * FROM AGGREGATE_ROOT_PROJECTION")) {
            resultSet.next();
            assertEquals("aggregateRootId", resultSet.getString("aggregaterootid"));
            assertEquals("aggregateRootType", resultSet.getString("aggregateroottype"));
            assertEquals("{\"@type\": \"AgroalDataSourcePostgreSqlAggregateRootProjectionRepositoryTest$TestAggregateRoot\", \"version\": 0, \"aggregateRootId\": \"aggregateRootId\", \"aggregateRootType\": \"aggregateRootType\"}", resultSet.getString("serializedaggregateroot"));
            assertEquals(0, resultSet.getLong("version"));
        }
    }

    @Test
    public void should_update_aggregate_root() throws SQLException {
        // Given
        final TestAggregateRoot testAggregateRoot = new TestAggregateRoot();
        testAggregateRoot.handle();
        aggregateRootProjectionRepository.merge(testAggregateRoot);
        testAggregateRoot.handle();

        // When
        aggregateRootProjectionRepository.merge(testAggregateRoot);

        // Then
        try (final Connection con = aggregateRootProjectionEventStoreDataSource.getConnection();
             final Statement stmt = con.createStatement();
             final ResultSet resultSet = stmt.executeQuery("SELECT * FROM AGGREGATE_ROOT_PROJECTION")) {
            resultSet.next();
            assertEquals("aggregateRootId", resultSet.getString("aggregaterootid"));
            assertEquals("aggregateRootType", resultSet.getString("aggregateroottype"));
            assertEquals("{\"@type\": \"AgroalDataSourcePostgreSqlAggregateRootProjectionRepositoryTest$TestAggregateRoot\", \"version\": 1, \"aggregateRootId\": \"aggregateRootId\", \"aggregateRootType\": \"aggregateRootType\"}", resultSet.getString("serializedaggregateroot"));
            assertEquals(1, resultSet.getLong("version"));
        }
    }
}
