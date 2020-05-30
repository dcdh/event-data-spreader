package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class PostgreSQLKafkaAggregateRootEventConsumedRepositoryTest {

    @Inject
    @DataSource("consumed-events")
    AgroalDataSource consumedEventsDataSource;

    @Inject
    PostgreSQLKafkaAggregateRootEventConsumedRepository postgreSQLKafkaAggregateRootEventConsumedRepository;

    @BeforeEach
    @AfterEach
    public void setup() {
        try (final Connection con = consumedEventsDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE CONSUMED_EVENT CASCADE");
            stmt.executeUpdate("TRUNCATE TABLE CONSUMED_EVENT_CONSUMER CASCADE");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void should_tables_be_initialised_at_application_startup() {
        assertDoesNotThrow(() -> {
            try (final Connection con = consumedEventsDataSource.getConnection();
                 final Statement stmt = con.createStatement();
                 final ResultSet rsConsumedEvent = stmt.executeQuery("SELECT * FROM CONSUMED_EVENT");
                 final ResultSet rsConsumedEventConsumer = stmt.executeQuery("SELECT * FROM CONSUMED_EVENT_CONSUMER")) {
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static class TestEventConsumer {

    }

    public static class TestAggregateRootEventId implements AggregateRootEventId {

        @Override
        public AggregateRootId aggregateRootId() {
            return new AggregateRootId() {
                @Override
                public String aggregateRootId() {
                    return "aggregateRootId";
                }

                @Override
                public String aggregateRootType() {
                    return "aggregateRootType";
                }
            };
        }

        @Override
        public Long version() {
            return 1l;
        }

    }

    public static class TestKafkaInfrastructureMetadata implements KafkaInfrastructureMetadata {

        @Override
        public Integer partition() {
            return 0;
        }

        @Override
        public String topic() {
            return "topic";
        }

        @Override
        public Long offset() {
            return 1l;
        }

    }

    @Test
    public void should_add_event_consumer_consumed_when_not_present() throws SQLException {
        // Given
        final LocalDateTime consumedAt = LocalDateTime.now();

        // When
        postgreSQLKafkaAggregateRootEventConsumedRepository.addEventConsumerConsumed(new TestAggregateRootEventId(), TestEventConsumer.class, consumedAt, new TestKafkaInfrastructureMetadata(), "gitCommitId");

        // Then
        try (final Connection con = consumedEventsDataSource.getConnection();
             final Statement stmt = con.createStatement();
             final ResultSet rsConsumedEvent = stmt.executeQuery("SELECT * FROM CONSUMED_EVENT")) {
            rsConsumedEvent.next();
            assertEquals("aggregateRootId", rsConsumedEvent.getString("aggregaterootid"));
            assertEquals("aggregateRootType", rsConsumedEvent.getString("aggregateroottype"));
            assertEquals(1l, rsConsumedEvent.getLong("version"));
            assertEquals(Boolean.FALSE, rsConsumedEvent.getBoolean("consumed"));
            assertEquals(consumedAt, rsConsumedEvent.getObject("consumedat", LocalDateTime.class));
            assertEquals(0, rsConsumedEvent.getInt("kafkapartition"));
            assertEquals("topic", rsConsumedEvent.getString("kafkatopic"));
            assertEquals(1l, rsConsumedEvent.getLong("kafkaoffset"));
        }
        try (final Connection con = consumedEventsDataSource.getConnection();
             final Statement stmt = con.createStatement();
             final ResultSet rsConsumedEventConsumer = stmt.executeQuery("SELECT * FROM CONSUMED_EVENT_CONSUMER")) {

            rsConsumedEventConsumer.next();
            assertEquals("aggregateRootId", rsConsumedEventConsumer.getString("aggregaterootid"));
            assertEquals("aggregateRootType", rsConsumedEventConsumer.getString("aggregateroottype"));
            assertEquals(1l, rsConsumedEventConsumer.getLong("version"));
            assertEquals("com.damdamdeo.eventsourced.consumer.infra.eventsourcing.PostgreSQLKafkaAggregateRootEventConsumedRepositoryTest$TestEventConsumer", rsConsumedEventConsumer.getString("consumerclassname"));
            assertEquals(consumedAt, rsConsumedEventConsumer.getObject("consumedat", LocalDateTime.class));
            assertEquals("gitCommitId", rsConsumedEventConsumer.getString("gitcommitid"));
        }
    }

    @Test
    public void should_update_event_consumer_consumed_when_present() throws SQLException {
        // Given
        final LocalDateTime consumedAt = LocalDateTime.now();

        try (final Connection connection = consumedEventsDataSource.getConnection();
             final PreparedStatement upsertEventConsumedPreparedStatement = connection.prepareStatement("INSERT INTO CONSUMED_EVENT (aggregaterootid, aggregateroottype, version, consumed, consumedat, kafkapartition, kafkatopic, kafkaoffset) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            upsertEventConsumedPreparedStatement.setString(1, "aggregateRootId");
            upsertEventConsumedPreparedStatement.setString(2, "aggregateRootType");
            upsertEventConsumedPreparedStatement.setLong(3, 1l);
            upsertEventConsumedPreparedStatement.setBoolean(4, Boolean.FALSE);
            upsertEventConsumedPreparedStatement.setObject(5, consumedAt);
            upsertEventConsumedPreparedStatement.setInt(6, 0);
            upsertEventConsumedPreparedStatement.setString(7, "topic");
            upsertEventConsumedPreparedStatement.setLong(8, 1l);
            upsertEventConsumedPreparedStatement.executeUpdate();
        }

        // When
        postgreSQLKafkaAggregateRootEventConsumedRepository.addEventConsumerConsumed(new TestAggregateRootEventId(), TestEventConsumer.class, consumedAt, new TestKafkaInfrastructureMetadata(), "gitCommitId");

        try (final Connection con = consumedEventsDataSource.getConnection();
             final Statement stmt = con.createStatement();
             final ResultSet rsConsumedEvent = stmt.executeQuery("SELECT * FROM CONSUMED_EVENT")) {
            rsConsumedEvent.next();
            assertEquals("aggregateRootId", rsConsumedEvent.getString("aggregaterootid"));
            assertEquals("aggregateRootType", rsConsumedEvent.getString("aggregateroottype"));
            assertEquals(1l, rsConsumedEvent.getLong("version"));
            assertEquals(Boolean.FALSE, rsConsumedEvent.getBoolean("consumed"));
            assertEquals(consumedAt, rsConsumedEvent.getObject("consumedat", LocalDateTime.class));
            assertEquals(0, rsConsumedEvent.getInt("kafkapartition"));
            assertEquals("topic", rsConsumedEvent.getString("kafkatopic"));
            assertEquals(1l, rsConsumedEvent.getLong("kafkaoffset"));
        }
        try (final Connection con = consumedEventsDataSource.getConnection();
             final Statement stmt = con.createStatement();
             final ResultSet rsConsumedEventConsumer = stmt.executeQuery("SELECT * FROM CONSUMED_EVENT_CONSUMER")) {

            rsConsumedEventConsumer.next();
            assertEquals("aggregateRootId", rsConsumedEventConsumer.getString("aggregaterootid"));
            assertEquals("aggregateRootType", rsConsumedEventConsumer.getString("aggregateroottype"));
            assertEquals(1l, rsConsumedEventConsumer.getLong("version"));
            assertEquals("com.damdamdeo.eventsourced.consumer.infra.eventsourcing.PostgreSQLKafkaAggregateRootEventConsumedRepositoryTest$TestEventConsumer", rsConsumedEventConsumer.getString("consumerclassname"));
            assertEquals(consumedAt, rsConsumedEventConsumer.getObject("consumedat", LocalDateTime.class));
            assertEquals("gitCommitId", rsConsumedEventConsumer.getString("gitcommitid"));
        }
    }

    @Test
    public void should_mark_event_as_consumed_when_no_consumer_has_been_involved() throws SQLException {
        // Given
        final LocalDateTime consumedAt = LocalDateTime.now();

        // When
        postgreSQLKafkaAggregateRootEventConsumedRepository.markEventAsConsumed(new TestAggregateRootEventId(), consumedAt, new TestKafkaInfrastructureMetadata());

        // Then
        try (final Connection con = consumedEventsDataSource.getConnection();
             final Statement stmt = con.createStatement();
             final ResultSet rsConsumedEvent = stmt.executeQuery("SELECT * FROM CONSUMED_EVENT")) {
            rsConsumedEvent.next();
            assertEquals("aggregateRootId", rsConsumedEvent.getString("aggregaterootid"));
            assertEquals("aggregateRootType", rsConsumedEvent.getString("aggregateroottype"));
            assertEquals(1l, rsConsumedEvent.getLong("version"));
            assertEquals(Boolean.TRUE, rsConsumedEvent.getBoolean("consumed"));
            assertEquals(consumedAt, rsConsumedEvent.getObject("consumedat", LocalDateTime.class));
            assertEquals(0, rsConsumedEvent.getInt("kafkapartition"));
            assertEquals("topic", rsConsumedEvent.getString("kafkatopic"));
            assertEquals(1l, rsConsumedEvent.getLong("kafkaoffset"));
        }
    }

    @Test
    public void should_mark_event_as_consumed_when_a_consumer_has_been_involved() throws SQLException {
        // Given
        final LocalDateTime consumedAt = LocalDateTime.now();
        postgreSQLKafkaAggregateRootEventConsumedRepository.addEventConsumerConsumed(new TestAggregateRootEventId(), TestEventConsumer.class, consumedAt, new TestKafkaInfrastructureMetadata(), "gitCommitId");

        // When
        postgreSQLKafkaAggregateRootEventConsumedRepository.markEventAsConsumed(new TestAggregateRootEventId(), consumedAt, new TestKafkaInfrastructureMetadata());

        // Then
        try (final Connection con = consumedEventsDataSource.getConnection();
             final Statement stmt = con.createStatement();
             final ResultSet rsConsumedEvent = stmt.executeQuery("SELECT * FROM CONSUMED_EVENT")) {
            rsConsumedEvent.next();
            assertEquals(Boolean.TRUE, rsConsumedEvent.getBoolean("consumed"));
        }
    }

    @Test
    public void should_has_not_finished_consuming_event_when_no_even_has_been_consumed() {
        // Given

        // When
        final Boolean hasConsumedEvent = postgreSQLKafkaAggregateRootEventConsumedRepository.hasFinishedConsumingEvent(new TestAggregateRootEventId());

        // Then
        assertFalse(hasConsumedEvent);
    }

    @Test
    public void should_has_not_finished_consuming_event_when_even_has_not_been_marked_as_consumed() {
        // Given
        final LocalDateTime consumedAt = LocalDateTime.now();
        postgreSQLKafkaAggregateRootEventConsumedRepository.addEventConsumerConsumed(new TestAggregateRootEventId(), TestEventConsumer.class, consumedAt, new TestKafkaInfrastructureMetadata(), "gitCommitId");

        // When
        final Boolean hasConsumedEvent = postgreSQLKafkaAggregateRootEventConsumedRepository.hasFinishedConsumingEvent(new TestAggregateRootEventId());

        // Then
        assertFalse(hasConsumedEvent);
    }

    @Test
    public void should_has_finished_consuming_event_when_even_has_been_marked_as_consumed() {
        // Given
        final LocalDateTime consumedAt = LocalDateTime.now();
        postgreSQLKafkaAggregateRootEventConsumedRepository.addEventConsumerConsumed(new TestAggregateRootEventId(), TestEventConsumer.class, consumedAt, new TestKafkaInfrastructureMetadata(), "gitCommitId");
        postgreSQLKafkaAggregateRootEventConsumedRepository.markEventAsConsumed(new TestAggregateRootEventId(), consumedAt, new TestKafkaInfrastructureMetadata());

        // When
        final Boolean hasConsumedEvent = postgreSQLKafkaAggregateRootEventConsumedRepository.hasFinishedConsumingEvent(new TestAggregateRootEventId());

        // Then
        assertTrue(hasConsumedEvent);
    }

    @Test
    public void should_return_consumers_having_processed_event_for_event() {
        // Given
        final LocalDateTime consumedAt = LocalDateTime.now();
        postgreSQLKafkaAggregateRootEventConsumedRepository.addEventConsumerConsumed(new TestAggregateRootEventId(), TestEventConsumer.class, consumedAt, new TestKafkaInfrastructureMetadata(), "gitCommitId");

        // When
        final List<String> consumersHavingProcessedEvent = postgreSQLKafkaAggregateRootEventConsumedRepository.getConsumersHavingProcessedEvent(new TestAggregateRootEventId());

        // Then
        assertEquals(Collections.singletonList("com.damdamdeo.eventsourced.consumer.infra.eventsourcing.PostgreSQLKafkaAggregateRootEventConsumedRepositoryTest$TestEventConsumer"),
                consumersHavingProcessedEvent);
    }

}
