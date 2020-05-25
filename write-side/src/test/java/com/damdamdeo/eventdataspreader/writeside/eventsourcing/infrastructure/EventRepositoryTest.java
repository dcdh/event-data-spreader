package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.*;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;

@QuarkusTest
public class EventRepositoryTest {

    @Inject
    @DataSource("aggregate-root-materialized-state")
    AgroalDataSource aggregateRootMaterializedStateDataSource;

    @Inject
    @DataSource("secret-store")
    AgroalDataSource secretStoreDataSource;

    @Inject
    EventRepository eventRepository;

    @InjectMock
    AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer;

    @InjectMock
    AggregateRootEventMetadataDeSerializer aggregateRootEventMetadataDeSerializer;

    @BeforeEach
    @AfterEach
    public void setupDatabases() {
        try (final Connection con = aggregateRootMaterializedStateDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE EVENT");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (final Connection con = secretStoreDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE SECRET_STORE");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void setupInjectedServicesMocks() {
        doReturn("{\"payload\": {}}").when(aggregateRootEventPayloadDeSerializer).serialize(any(), any());
        doReturn("{\"meta\": {}}").when(aggregateRootEventMetadataDeSerializer).serialize(any(), any());

        doReturn(new TestAggregateRootEventPayload("dummy")).when(aggregateRootEventPayloadDeSerializer).deserialize(any(), any());
        doReturn(new TestAggregateRootEventMetadata("dummy")).when(aggregateRootEventMetadataDeSerializer).deserialize(any(), any());
    }

    @AfterEach
    public void tearDown() {
        reset(aggregateRootEventPayloadDeSerializer, aggregateRootEventMetadataDeSerializer);
    }

    public static final class TestAggregateRoot extends AggregateRoot {}

    public static final class TestAggregateRootEventMetadata extends AggregateRootEventMetadata {

        private final String dummy;

        public TestAggregateRootEventMetadata(final String dummy) {
            this.dummy = dummy;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestAggregateRootEventMetadata that = (TestAggregateRootEventMetadata) o;
            return Objects.equals(dummy, that.dummy);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dummy);
        }
    }

    public static final class TestAggregateRootEventPayload extends AggregateRootEventPayload<TestAggregateRoot> {

        private final String dummy;

        public TestAggregateRootEventPayload(final String dummy) {
            this.dummy = dummy;
        }

        @Override
        public void apply(TestAggregateRoot aggregateRoot) {

        }

        @Override
        public String eventPayloadName() {
            return "TestAggregateRootEventPayload";
        }

        @Override
        public String aggregateRootId() {
            return "aggregateRootId";
        }

        @Override
        public String aggregateRootType() {
            return "TestAggregateRoot";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestAggregateRootEventPayload that = (TestAggregateRootEventPayload) o;
            return Objects.equals(dummy, that.dummy);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dummy);
        }
    }

    @Test
    public void should_tables_be_initialised_at_application_startup() {
        Assertions.assertDoesNotThrow(() -> {
            try (final Connection con = aggregateRootMaterializedStateDataSource.getConnection();
                 final Statement stmt = con.createStatement();
                 final ResultSet rsEvent = stmt.executeQuery("SELECT * FROM EVENT")) {
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void should_save_event_with_generated_encrypted_key() throws SQLException {
        // Given
        final LocalDateTime creationDate = LocalDateTime.now();

        final AggregateRootEvent aggregateRootEvent = new AggregateRootEvent(new EventSourcedAggregateRootEventId("aggregateRootId", "aggregateRootType", 0l),
                "eventType",
                creationDate,
                new TestAggregateRootEventPayload("dummy"),
                new TestAggregateRootEventMetadata("dummy"));

        // When
        eventRepository.save(Collections.singletonList(aggregateRootEvent));

        // Then
        try (final Connection con = aggregateRootMaterializedStateDataSource.getConnection();
             final Statement stmt = con.createStatement();
             final ResultSet resultSet = stmt.executeQuery("SELECT * FROM EVENT")) {
            resultSet.next();
            assertEquals("aggregateRootId", resultSet.getString("aggregaterootid"));
            assertEquals("aggregateRootType", resultSet.getString("aggregateroottype"));
            assertEquals(0, resultSet.getLong("version"));
            assertEquals(creationDate, resultSet.getObject("creationdate", LocalDateTime.class));
            assertEquals("{\"meta\": {}}", resultSet.getString("eventmetadata"));
            assertEquals("{\"payload\": {}}", resultSet.getString("eventpayload"));
        }
        try (final Connection con = secretStoreDataSource.getConnection();
             final Statement stmt = con.createStatement();
             final ResultSet resultSet = stmt.executeQuery("SELECT * FROM SECRET_STORE")) {
            resultSet.next();
            assertEquals("aggregateRootId", resultSet.getString("aggregaterootid"));
            assertEquals("aggregateRootType", resultSet.getString("aggregateroottype"));
            assertNotNull(resultSet.getString("secret"));
        }
    }

    @Test
    public void should_use_same_key_for_aggregate_with_multiple_events() throws SQLException {
        // Given
        final LocalDateTime creationDate = LocalDateTime.now();

        final AggregateRootEvent aggregateRootEvent0 = new AggregateRootEvent(new EventSourcedAggregateRootEventId("aggregateRootId", "aggregateRootType", 0l),
                "eventType",
                creationDate,
                new TestAggregateRootEventPayload("dummy"),
                new TestAggregateRootEventMetadata("dummy"));
        eventRepository.save(Collections.singletonList(aggregateRootEvent0));

        final AggregateRootEvent aggregateRootEvent1 = new AggregateRootEvent(new EventSourcedAggregateRootEventId("aggregateRootId", "aggregateRootType", 1l),
                "eventType",
                creationDate,
                new TestAggregateRootEventPayload("dummy"),
                new TestAggregateRootEventMetadata("dummy"));

        // When
        eventRepository.save(Collections.singletonList(aggregateRootEvent1));

        // Then
        try (final Connection con = aggregateRootMaterializedStateDataSource.getConnection();
             final Statement stmt = con.createStatement();
             final ResultSet resultSet = stmt.executeQuery("SELECT COUNT(*) AS nbEvents FROM EVENT")) {
            resultSet.next();
            assertEquals(2, resultSet.getLong("nbEvents"));
        }
        try (final Connection con = secretStoreDataSource.getConnection();
             final Statement stmt = con.createStatement();
             final ResultSet resultSet = stmt.executeQuery("SELECT COUNT(*) AS nbSecrets FROM SECRET_STORE")) {
            resultSet.next();
            assertEquals(1, resultSet.getLong("nbSecrets"));
        }
    }

    @Test
    public void should_load_events_ordered_by_version_asc() {
        // Given
        final LocalDateTime creationDate = LocalDateTime.now();

        final AggregateRootEvent aggregateRootEvent = new AggregateRootEvent(new EventSourcedAggregateRootEventId("aggregateRootId", "aggregateRootType", 0l),
                "eventType",
                creationDate,
                new TestAggregateRootEventPayload("dummy"),
                new TestAggregateRootEventMetadata("dummy"));

        eventRepository.save(Arrays.asList(aggregateRootEvent));

        // When
        final List<AggregateRootEvent> aggregateRootEvents = eventRepository.loadOrderByVersionASC("aggregateRootId", "aggregateRootType");

        // Then
        assertEquals(Arrays.asList(
                new AggregateRootEvent(new PostgreSQLAggregateRootEventId(new EventSourcedAggregateRootEventId("aggregateRootId", "aggregateRootType", 0l)),
                        "eventType", creationDate, new TestAggregateRootEventPayload("dummy"), new TestAggregateRootEventMetadata("dummy")))
                , aggregateRootEvents);
    }

}
