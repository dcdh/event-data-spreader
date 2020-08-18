package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.*;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventMetadataDeSerializer;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventPayloadsDeSerializer;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootMaterializedStatesSerializer;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@QuarkusTest
public class PostgreSQLEventRepositoryTest {

    @Inject
    @DataSource("mutable")
    AgroalDataSource mutableDataSource;

    @Inject
    PostgreSQLEventRepository eventRepository;

    @InjectMock
    AggregateRootEventPayloadsDeSerializer aggregateRootEventPayloadsDeSerializer;

    @InjectMock
    AggregateRootEventMetadataDeSerializer aggregateRootEventMetadataDeSerializer;

    @InjectMock
    AggregateRootMaterializedStatesSerializer aggregateRootMaterializedStatesSerializer;

    @InjectMock
    GitCommitProvider gitCommitProvider;

    @InjectMock
    SecretStore secretStore;

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
        doReturn("{\"payload\": {}}").when(aggregateRootEventPayloadsDeSerializer).serialize(any(), any(), any());
        doReturn("{\"meta\": {}}").when(aggregateRootEventMetadataDeSerializer).serialize();
        doReturn("{\"materializedState\": {}}").when(aggregateRootMaterializedStatesSerializer).serialize(any(), anyBoolean());
        doReturn(mock(Secret.class)).when(secretStore).read(any());
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
        doReturn("3bc9898721c64c5d6d17724bf6ec1c715cca0f69").when(gitCommitProvider).gitCommitId();
        final LocalDateTime creationDate = LocalDateTime.now();

        final AggregateRootEventId aggregateRootEventId = mock(AggregateRootEventId.class, RETURNS_DEEP_STUBS);
        when(aggregateRootEventId.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRootEventId.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(aggregateRootEventId.version()).thenReturn(0l);
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class);

        final AggregateRootEvent aggregateRootEvent = new AggregateRootEvent(
                aggregateRootEventId,
                "eventType",
                creationDate,
                aggregateRootEventPayload);
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(aggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRoot.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(aggregateRoot.version()).thenReturn(0l);

        // When
        eventRepository.save(aggregateRootEvent, aggregateRoot);

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
            assertEquals("aggregateRootType", resultSet.getString("aggregateroottype"));
            assertEquals(0, resultSet.getLong("version"));
            assertEquals(creationDate, resultSet.getObject("creationdate", LocalDateTime.class));
            assertEquals("{\"meta\": {}}", resultSet.getString("eventmetadata"));
            assertEquals("{\"payload\": {}}", resultSet.getString("eventpayload"));
            assertEquals("{\"materializedState\": {}}", resultSet.getString("materializedstate"));
            assertEquals("3bc9898721c64c5d6d17724bf6ec1c715cca0f69", resultSet.getString("gitcommitid"));
        }
        verify(gitCommitProvider, atLeastOnce()).gitCommitId();
        verify(aggregateRootEventId.aggregateRootId(), atLeastOnce()).aggregateRootId();
        verify(aggregateRootEventId.aggregateRootId(), atLeastOnce()).aggregateRootType();
        verify(aggregateRootEventId, atLeastOnce()).version();
        verify(aggregateRoot.aggregateRootId(), atLeastOnce()).aggregateRootId();
        verify(aggregateRoot.aggregateRootId(), atLeastOnce()).aggregateRootType();
        verify(aggregateRoot, atLeastOnce()).version();
    }

    @Test
    public void should_use_same_key_for_aggregate_with_multiple_events() throws SQLException {
        // Given
        doReturn("3bc9898721c64c5d6d17724bf6ec1c715cca0f69").when(gitCommitProvider).gitCommitId();
        final LocalDateTime creationDate = LocalDateTime.now();

        final AggregateRootEventId aggregateRootEventId0 = mock(AggregateRootEventId.class, RETURNS_DEEP_STUBS);
        when(aggregateRootEventId0.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRootEventId0.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(aggregateRootEventId0.version()).thenReturn(0l);
        final AggregateRootEventPayload aggregateRootEventPayload0 = mock(AggregateRootEventPayload.class);

        final AggregateRootEvent aggregateRootEvent0 = new AggregateRootEvent(
                aggregateRootEventId0,
                "eventType",
                creationDate,
                aggregateRootEventPayload0);
        final AggregateRoot aggregateRoot0 = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(aggregateRoot0.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRoot0.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(aggregateRoot0.version()).thenReturn(0l);

        eventRepository.save(aggregateRootEvent0, aggregateRoot0);

        final AggregateRootEventId aggregateRootEventId1 = mock(AggregateRootEventId.class, RETURNS_DEEP_STUBS);
        when(aggregateRootEventId1.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRootEventId1.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(aggregateRootEventId1.version()).thenReturn(1l);
        final AggregateRootEventPayload aggregateRootEventPayload1 = mock(AggregateRootEventPayload.class);

        final AggregateRootEvent aggregateRootEvent1 = new AggregateRootEvent(
                aggregateRootEventId1,
                "eventType",
                creationDate,
                aggregateRootEventPayload1);
        final AggregateRoot aggregateRoot1 = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(aggregateRoot1.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRoot1.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(aggregateRoot1.version()).thenReturn(1l);

        // When
        eventRepository.save(aggregateRootEvent1, aggregateRoot1);

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
        doReturn("3bc9898721c64c5d6d17724bf6ec1c715cca0f69").when(gitCommitProvider).gitCommitId();
        final LocalDateTime creationDate = LocalDateTime.now();

        final AggregateRootEventId aggregateRootEventId = mock(AggregateRootEventId.class, RETURNS_DEEP_STUBS);
        when(aggregateRootEventId.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRootEventId.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(aggregateRootEventId.version()).thenReturn(0l);
        final AggregateRootEventPayload aggregateRootEventPayload = mock(AggregateRootEventPayload.class);

        final AggregateRootEvent aggregateRootEvent = new AggregateRootEvent(
                aggregateRootEventId,
                "eventType",
                creationDate,
                aggregateRootEventPayload);
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(aggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRoot.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(aggregateRoot.version()).thenReturn(0l);

        doReturn(aggregateRootEventPayload).when(aggregateRootEventPayloadsDeSerializer).deserialize(any(), any(), any());

        eventRepository.save(aggregateRootEvent, aggregateRoot);

        // When
        final List<AggregateRootEvent> aggregateRootEvents = eventRepository.loadOrderByVersionASC(new ApiAggregateRootId("aggregateRootId", "aggregateRootType"));

        // Then
        assertEquals(asList(
                new AggregateRootEvent(new PostgreSQLAggregateRootEventId(
                        new DefaultAggregateRootEventId(new PostgreSQLAggregateRootId("aggregateRootId", "aggregateRootType"), 0l)),
                        "eventType", creationDate, aggregateRootEventPayload)
                ),
                aggregateRootEvents);
        verify(aggregateRootEventPayloadsDeSerializer, times(1)).deserialize(any(), any(), any());
    }

    @Test
    public void should_load_events_ordered_by_version_asc_with_expected_versions() {
        // Given
        doReturn("3bc9898721c64c5d6d17724bf6ec1c715cca0f69").when(gitCommitProvider).gitCommitId();
        final LocalDateTime creationDate0 = LocalDateTime.now();

        final AggregateRootEventId aggregateRootEventId0 = mock(AggregateRootEventId.class, RETURNS_DEEP_STUBS);
        when(aggregateRootEventId0.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRootEventId0.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(aggregateRootEventId0.version()).thenReturn(0l);
        final AggregateRootEventPayload aggregateRootEventPayload0 = mock(AggregateRootEventPayload.class);

        final AggregateRootEvent aggregateRootEvent0 = new AggregateRootEvent(
                aggregateRootEventId0,
                "eventType",
                creationDate0,
                aggregateRootEventPayload0);
        final AggregateRoot aggregateRoot0 = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(aggregateRoot0.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRoot0.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(aggregateRoot0.version()).thenReturn(0l);

        final LocalDateTime creationDate1 = LocalDateTime.now();
        final AggregateRootEventId aggregateRootEventId1 = mock(AggregateRootEventId.class, RETURNS_DEEP_STUBS);
        when(aggregateRootEventId1.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRootEventId1.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(aggregateRootEventId1.version()).thenReturn(1l);
        final AggregateRootEventPayload aggregateRootEventPayload1 = mock(AggregateRootEventPayload.class);

        final AggregateRootEvent aggregateRootEvent1 = new AggregateRootEvent(
                aggregateRootEventId1,
                "eventType",
                creationDate1,
                aggregateRootEventPayload1);
        final AggregateRoot aggregateRoot1 = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(aggregateRoot1.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRoot1.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(aggregateRoot1.version()).thenReturn(1l);

        final LocalDateTime creationDate2 = LocalDateTime.now();
        final AggregateRootEventId aggregateRootEventId2 = mock(AggregateRootEventId.class, RETURNS_DEEP_STUBS);
        when(aggregateRootEventId2.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRootEventId2.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(aggregateRootEventId2.version()).thenReturn(2l);
        final AggregateRootEventPayload aggregateRootEventPayload2 = mock(AggregateRootEventPayload.class);

        final AggregateRootEvent aggregateRootEvent2 = new AggregateRootEvent(
                aggregateRootEventId2,
                "eventType",
                creationDate2,
                aggregateRootEventPayload2);
        final AggregateRoot aggregateRoot2 = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(aggregateRoot2.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(aggregateRoot2.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(aggregateRoot2.version()).thenReturn(2l);

        doReturn(aggregateRootEventPayload0)
                .doReturn(aggregateRootEventPayload1)
                .doReturn(aggregateRootEventPayload2)
                .when(aggregateRootEventPayloadsDeSerializer).deserialize(any(), any(), any());

        eventRepository.save(aggregateRootEvent0, aggregateRoot0);
        eventRepository.save(aggregateRootEvent1, aggregateRoot1);
        eventRepository.save(aggregateRootEvent2, aggregateRoot2);

        // When
        final List<AggregateRootEvent> aggregateRootEvents = eventRepository.loadOrderByVersionASC(new ApiAggregateRootId("aggregateRootId", "aggregateRootType"), 1l);

        // Then
        assertEquals(asList(
                new AggregateRootEvent(new PostgreSQLAggregateRootEventId(
                        new DefaultAggregateRootEventId(new PostgreSQLAggregateRootId("aggregateRootId", "aggregateRootType"), 0l)),
                        "eventType", creationDate0, aggregateRootEventPayload0),
                new AggregateRootEvent(new PostgreSQLAggregateRootEventId(
                        new DefaultAggregateRootEventId(new PostgreSQLAggregateRootId("aggregateRootId", "aggregateRootType"), 1l)),
                        "eventType", creationDate1, aggregateRootEventPayload1)
                ),
                aggregateRootEvents);

        verify(aggregateRootEventPayloadsDeSerializer, times(2)).deserialize(any(), any(), any());
    }

    @Test
    public void should_fail_fast_when_aggregate_id_does_not_match_event_aggregate_id() {
        // Given
        final AggregateRootEvent mockAggregateRootEvent = mock(AggregateRootEvent.class, RETURNS_DEEP_STUBS);
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(mockAggregateRootEvent.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId0");
        when(mockAggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId1");

        // When && Then
        assertThrows(IllegalStateException.class, () -> eventRepository.save(mockAggregateRootEvent, mockAggregateRoot));

        verify(mockAggregateRootEvent.aggregateRootId(), times(1)).aggregateRootId();
        verify(mockAggregateRoot.aggregateRootId(), times(1)).aggregateRootId();
    }

    @Test
    public void should_fail_fast_when_aggregate_type_does_not_match_event_aggregate_type() {
        // Given
        final AggregateRootEvent mockAggregateRootEvent = mock(AggregateRootEvent.class, RETURNS_DEEP_STUBS);
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(mockAggregateRootEvent.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(mockAggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(mockAggregateRootEvent.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType0");
        when(mockAggregateRoot.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType1");

        // When && Then
        assertThrows(IllegalStateException.class, () -> eventRepository.save(mockAggregateRootEvent, mockAggregateRoot));

        verify(mockAggregateRootEvent.aggregateRootId(), times(1)).aggregateRootId();
        verify(mockAggregateRoot.aggregateRootId(), times(1)).aggregateRootId();
        verify(mockAggregateRootEvent.aggregateRootId(), times(1)).aggregateRootType();
        verify(mockAggregateRoot.aggregateRootId(), times(1)).aggregateRootType();
    }

    @Test
    public void should_fail_fast_when_aggregate_version_does_not_match_event_aggregate_version() {
        // Given
        final AggregateRootEvent mockAggregateRootEvent = mock(AggregateRootEvent.class, RETURNS_DEEP_STUBS);
        final AggregateRoot mockAggregateRoot = mock(AggregateRoot.class, RETURNS_DEEP_STUBS);
        when(mockAggregateRootEvent.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(mockAggregateRoot.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(mockAggregateRootEvent.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(mockAggregateRoot.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        when(mockAggregateRootEvent.version()).thenReturn(1l);
        when(mockAggregateRoot.version()).thenReturn(0l);

        // When && Then
        assertThrows(IllegalStateException.class, () -> eventRepository.save(mockAggregateRootEvent, mockAggregateRoot));

        verify(mockAggregateRootEvent.aggregateRootId(), times(1)).aggregateRootId();
        verify(mockAggregateRoot.aggregateRootId(), times(1)).aggregateRootId();
        verify(mockAggregateRootEvent.aggregateRootId(), times(1)).aggregateRootType();
        verify(mockAggregateRoot.aggregateRootId(), times(1)).aggregateRootType();
        verify(mockAggregateRootEvent, times(1)).version();
        verify(mockAggregateRoot, times(1)).version();
    }

}
