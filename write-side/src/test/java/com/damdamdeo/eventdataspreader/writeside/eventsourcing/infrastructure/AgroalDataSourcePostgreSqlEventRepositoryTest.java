package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.writeside.aggregate.event.DefaultEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.Event;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventRepository;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class AgroalDataSourcePostgreSqlEventRepositoryTest {

    @Inject
    @DataSource("aggregate-root-projection-event-store")
    AgroalDataSource aggregateRootProjectionEventStoreDataSource;

    @Inject
    @DataSource("secret-store")
    AgroalDataSource secretStoreDataSource;

    @Inject
    EventRepository eventRepository;

    @BeforeEach
    public void setup() {
        try (final Connection con = aggregateRootProjectionEventStoreDataSource.getConnection();
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

    @Test
    public void should_tables_be_initialised_at_application_startup() {
        Assertions.assertDoesNotThrow(() -> {
            try (final Connection con = aggregateRootProjectionEventStoreDataSource.getConnection();
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

        final Event event = new Event("aggregateRootId", "aggregateRootType", "eventType", 0l,
                creationDate,
                new TestAggregateRootEventPayload("dummy"),
                new DefaultEventMetadata("executedBy"));

        // When
        eventRepository.save(Collections.singletonList(event));

        // Then
        try (final Connection con = aggregateRootProjectionEventStoreDataSource.getConnection();
             final Statement stmt = con.createStatement();
             final ResultSet resultSet = stmt.executeQuery("SELECT * FROM EVENT")) {
            resultSet.next();
            assertEquals("aggregateRootId", resultSet.getString("aggregaterootid"));
            assertEquals("aggregateRootType", resultSet.getString("aggregateroottype"));
            assertEquals(0, resultSet.getLong("version"));
            assertEquals(creationDate, resultSet.getObject("creationdate", LocalDateTime.class));
            assertEquals("{\"@type\": \"DefaultEventMetadata\", \"executedBy\": \"executedBy\"}", resultSet.getString("eventmetadata"));
            assertEquals("{\"@type\": \"TestAggregateRootEventPayload\", \"dummy\": \"dummy\"}", resultSet.getString("eventpayload"));
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

        final Event event0 = new Event("aggregateRootId", "aggregateRootType", "eventType", 0l,
                creationDate,
                new TestAggregateRootEventPayload("dummy0"),
                new DefaultEventMetadata("executedBy0"));
        eventRepository.save(Collections.singletonList(event0));

        final Event event1 = new Event("aggregateRootId", "aggregateRootType", "eventType", 1l,
                creationDate,
                new TestAggregateRootEventPayload("dummy1"),
                new DefaultEventMetadata("executedBy1"));

        // When
        eventRepository.save(Collections.singletonList(event1));

        // Then
        try (final Connection con = aggregateRootProjectionEventStoreDataSource.getConnection();
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

        final Event event0 = new Event("aggregateRootId", "aggregateRootType", "eventType", 0l,
                creationDate,
                new TestAggregateRootEventPayload("dummy0"),
                new DefaultEventMetadata("executedBy0"));

        final Event event1 = new Event("aggregateRootId", "aggregateRootType", "eventType", 1l,
                creationDate,
                new TestAggregateRootEventPayload("dummy1"),
                new DefaultEventMetadata("executedBy1"));
        eventRepository.save(Arrays.asList(event0, event1));

        // When
        final List<Event> events = eventRepository.loadOrderByVersionASC("aggregateRootId", "aggregateRootType");

        // Then
        assertEquals(Arrays.asList(
                new Event("aggregateRootId", "aggregateRootType", "eventType", 0l, creationDate, new TestAggregateRootEventPayload("dummy0"), new DefaultEventMetadata("executedBy0")),
                new Event("aggregateRootId", "aggregateRootType", "eventType", 1l, creationDate, new TestAggregateRootEventPayload("dummy1"), new DefaultEventMetadata("executedBy1")))
                , events);
    }

}
