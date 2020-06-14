package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRootEvent;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRootMaterializedStateSerializer;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.*;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;

@QuarkusTest
public class PostgreSQLEventRepositoryTest {

    @Inject
    @DataSource("mutable")
    AgroalDataSource mutableDataSource;

    @Inject
    PostgreSQLEventRepository eventRepository;

    @InjectMock
    AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer;

    @InjectMock
    AggregateRootEventMetadataDeSerializer aggregateRootEventMetadataDeSerializer;

    @InjectMock
    AggregateRootMaterializedStateSerializer aggregateRootMaterializedStateSerializer;

    @BeforeEach
    public void setupEncryption() {
        final AggregateRootSecret aggregateRootSecret = new AggregateRootSecret() {
            @Override
            public AggregateRootId aggregateRootId() {
                return new TestAggregateRootId("aggregateRootId", "aggregateRootType");
            }

            @Override
            public String secret() {
                return null;
            }
        };
    }

    @BeforeEach
    @AfterEach
    public void setupDatabases() {
        try (final Connection con = mutableDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE EVENT");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void setupInjectedServicesMocks() {
        doReturn("{\"payload\": {}}").when(aggregateRootEventPayloadDeSerializer).serialize(any(), any());
        doReturn("{\"meta\": {}}").when(aggregateRootEventMetadataDeSerializer).serialize(any(), any());
        doReturn("{\"materializedState\": {}}").when(aggregateRootMaterializedStateSerializer).serialize(any(), any());
        doReturn(new TestAggregateRootEventPayload("dummy")).when(aggregateRootEventPayloadDeSerializer).deserialize(any(), any());
        doReturn(new TestAggregateRootEventMetadata("dummy")).when(aggregateRootEventMetadataDeSerializer).deserialize(any(), any());
    }

    @AfterEach
    public void tearDown() {
        reset(aggregateRootEventPayloadDeSerializer, aggregateRootEventMetadataDeSerializer);
    }

    public static final class TestAggregateRootId implements AggregateRootId {

        private final String aggregateRootId;
        private final String aggregateRootType;

        public TestAggregateRootId(final String aggregateRootId, final String aggregateRootType) {
            this.aggregateRootId = aggregateRootId;
            this.aggregateRootType = aggregateRootType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestAggregateRootId that = (TestAggregateRootId) o;
            return Objects.equals(aggregateRootId, that.aggregateRootId) &&
                    Objects.equals(aggregateRootType, that.aggregateRootType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(aggregateRootId, aggregateRootType);
        }

        @Override
        public String aggregateRootId() {
            return aggregateRootId;
        }

        @Override
        public String aggregateRootType() {
            return aggregateRootType;
        }
    }

    public static final class TestAggregateRoot extends AggregateRoot {

        public void apply(final TestAggregateRootEventPayload testAggregateRootEventPayload,
                          final TestAggregateRootEventMetadata testAggregateRootEventMetadata) {
            super.apply(testAggregateRootEventPayload, testAggregateRootEventMetadata);
        }

    }

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
        public AggregateRootId aggregateRootId() {
            return new TestAggregateRootId("aggregateRootId", "TestAggregateRoot");
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
            try (final Connection con = mutableDataSource.getConnection();
                 final Statement stmt = con.createStatement();
                 final ResultSet rsEvent = stmt.executeQuery("SELECT * FROM EVENT")) {
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void should_save_event() throws SQLException {
        // Given
        final LocalDateTime creationDate = LocalDateTime.now();

        final AggregateRootEvent aggregateRootEvent = new AggregateRootEvent(
                new DefaultAggregateRootEventId(new TestAggregateRootId("aggregateRootId", "TestAggregateRoot"), 0l),
                "eventType",
                creationDate,
                new TestAggregateRootEventPayload("dummy"),
                new TestAggregateRootEventMetadata("dummy"));

        // When
        eventRepository.save(Collections.singletonList(aggregateRootEvent), Optional.empty());

        // Then
        try (final Connection con = mutableDataSource.getConnection();
             final Statement stmt = con.createStatement();
             final ResultSet resultSet = stmt.executeQuery("SELECT COUNT(*) AS count FROM EVENT")) {
            resultSet.next();
            assertEquals(1l, resultSet.getLong("count"));
        }
        try (final Connection con = mutableDataSource.getConnection();
             final Statement stmt = con.createStatement();
             final ResultSet resultSet = stmt.executeQuery("SELECT * FROM EVENT")) {
            resultSet.next();
            assertEquals("aggregateRootId", resultSet.getString("aggregaterootid"));
            assertEquals("TestAggregateRoot", resultSet.getString("aggregateroottype"));
            assertEquals(0, resultSet.getLong("version"));
            assertEquals(creationDate, resultSet.getObject("creationdate", LocalDateTime.class));
            assertEquals("{\"meta\": {}}", resultSet.getString("eventmetadata"));
            assertEquals("{\"payload\": {}}", resultSet.getString("eventpayload"));
            assertNull(resultSet.getString("materializedstate"));
        }
    }

    @Test
    public void should_save_materialized_state() throws SQLException {
        // Given
        final AggregateRootId aggregateRootId = new TestAggregateRootId("aggregateRootId", "TestAggregateRoot");
        final TestAggregateRootEventPayload testAggregateRootEventPayload = new TestAggregateRootEventPayload("dummy");
        final TestAggregateRootEventMetadata testAggregateRootEventMetadata = new TestAggregateRootEventMetadata("dummy");
        final AggregateRootEvent aggregateRootEvent = new AggregateRootEvent(
                new DefaultAggregateRootEventId(aggregateRootId, 0l),
                "eventType",
                LocalDateTime.now(),
                testAggregateRootEventPayload,
                testAggregateRootEventMetadata);
        eventRepository.save(Collections.singletonList(aggregateRootEvent), Optional.empty());
        final TestAggregateRoot testAggregateRoot = new TestAggregateRoot();
        testAggregateRoot.apply(testAggregateRootEventPayload, testAggregateRootEventMetadata);

        // When
        eventRepository.saveMaterializedState(testAggregateRoot, Optional.empty());

        // Then
        try (final Connection con = mutableDataSource.getConnection();
             final Statement stmt = con.createStatement();
             final ResultSet resultSet = stmt.executeQuery("SELECT COUNT(*) AS count FROM EVENT")) {
            resultSet.next();
            assertEquals(1l, resultSet.getLong("count"));
        }
        try (final Connection con = mutableDataSource.getConnection();
             final Statement stmt = con.createStatement();
             final ResultSet resultSet = stmt.executeQuery("SELECT * FROM EVENT")) {
            resultSet.next();
            assertEquals("aggregateRootId", resultSet.getString("aggregaterootid"));
            assertEquals("TestAggregateRoot", resultSet.getString("aggregateRootType"));
            assertEquals("{\"materializedState\": {}}", resultSet.getString("materializedstate"));
        }
    }

    @Test
    public void should_use_same_key_for_aggregate_with_multiple_events() throws SQLException {
        // Given
        final LocalDateTime creationDate = LocalDateTime.now();

        final AggregateRootEvent aggregateRootEvent0 = new AggregateRootEvent(
                new DefaultAggregateRootEventId(new TestAggregateRootId("aggregateRootId", "aggregateRootType"), 0l),
                "eventType",
                creationDate,
                new TestAggregateRootEventPayload("dummy"),
                new TestAggregateRootEventMetadata("dummy"));
        eventRepository.save(Collections.singletonList(aggregateRootEvent0), Optional.empty());

        final AggregateRootEvent aggregateRootEvent1 = new AggregateRootEvent(
                new DefaultAggregateRootEventId(new TestAggregateRootId("aggregateRootId", "aggregateRootType"), 1l),
                "eventType",
                creationDate,
                new TestAggregateRootEventPayload("dummy"),
                new TestAggregateRootEventMetadata("dummy"));

        // When
        eventRepository.save(Collections.singletonList(aggregateRootEvent1), Optional.empty());

        // Then
        try (final Connection con = mutableDataSource.getConnection();
             final Statement stmt = con.createStatement();
             final ResultSet resultSet = stmt.executeQuery("SELECT COUNT(*) AS nbEvents FROM EVENT")) {
            resultSet.next();
            assertEquals(2, resultSet.getLong("nbEvents"));
        }
    }

    @Test
    public void should_load_events_ordered_by_version_asc() {
        // Given
        final LocalDateTime creationDate = LocalDateTime.now();

        final AggregateRootEvent aggregateRootEvent = new AggregateRootEvent(
                new DefaultAggregateRootEventId(new TestAggregateRootId("aggregateRootId", "aggregateRootType"), 0l),
                "eventType",
                creationDate,
                new TestAggregateRootEventPayload("dummy"),
                new TestAggregateRootEventMetadata("dummy"));

        eventRepository.save(Arrays.asList(aggregateRootEvent), Optional.empty());

        // When
        final List<AggregateRootEvent> aggregateRootEvents = eventRepository.loadOrderByVersionASC("aggregateRootId", "aggregateRootType", Optional.empty());

        // Then
        assertEquals(Arrays.asList(
                new AggregateRootEvent(new PostgreSQLAggregateRootEventId(
                        new DefaultAggregateRootEventId(new TestAggregateRootId("aggregateRootId", "aggregateRootType"), 0l)),
                        "eventType", creationDate, new TestAggregateRootEventPayload("dummy"), new TestAggregateRootEventMetadata("dummy"))
                ),
                aggregateRootEvents);
    }

    @Test
    public void should_load_events_ordered_by_version_asc_with_expected_versions() {
        // Given
        final LocalDateTime creationDate0 = LocalDateTime.now();

        final AggregateRootEvent aggregateRootEvent0 = new AggregateRootEvent(
                new DefaultAggregateRootEventId(new TestAggregateRootId("aggregateRootId", "aggregateRootType"), 0l),
                "eventType",
                creationDate0,
                new TestAggregateRootEventPayload("dummy"),
                new TestAggregateRootEventMetadata("dummy"));

        final LocalDateTime creationDate1 = LocalDateTime.now();

        final AggregateRootEvent aggregateRootEvent1 = new AggregateRootEvent(
                new DefaultAggregateRootEventId(new TestAggregateRootId("aggregateRootId", "aggregateRootType"), 1l),
                "eventType",
                creationDate1,
                new TestAggregateRootEventPayload("dummy"),
                new TestAggregateRootEventMetadata("dummy"));

        final LocalDateTime creationDate2 = LocalDateTime.now();

        final AggregateRootEvent aggregateRootEvent2 = new AggregateRootEvent(
                new DefaultAggregateRootEventId(new TestAggregateRootId("aggregateRootId", "aggregateRootType"), 2l),
                "eventType",
                creationDate2,
                new TestAggregateRootEventPayload("dummy"),
                new TestAggregateRootEventMetadata("dummy"));

        eventRepository.save(Arrays.asList(aggregateRootEvent0, aggregateRootEvent1, aggregateRootEvent2), Optional.empty());

        // When
        final List<AggregateRootEvent> aggregateRootEvents = eventRepository.loadOrderByVersionASC("aggregateRootId", "aggregateRootType", Optional.empty(), 1l);

        // Then
        assertEquals(Arrays.asList(
                new AggregateRootEvent(new PostgreSQLAggregateRootEventId(
                        new DefaultAggregateRootEventId(new TestAggregateRootId("aggregateRootId", "aggregateRootType"), 0l)),
                        "eventType", creationDate0, new TestAggregateRootEventPayload("dummy"), new TestAggregateRootEventMetadata("dummy")),
                new AggregateRootEvent(new PostgreSQLAggregateRootEventId(
                        new DefaultAggregateRootEventId(new TestAggregateRootId("aggregateRootId", "aggregateRootType"), 1l)),
                        "eventType", creationDate1, new TestAggregateRootEventPayload("dummy"), new TestAggregateRootEventMetadata("dummy"))
                ),
                aggregateRootEvents);
    }

}
