package com.damdamdeo.eventdataspreader.event.infrastructure.consumer;

import com.damdamdeo.eventdataspreader.event.api.AggregateRootEventId;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.runtime.Startup;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

// I could used entities to do some business cases like marked an event as consumed. However it will imply a lot of mapping ...

@Startup
@ApplicationScoped
public class AgroalDataSourcePostgreSqlAggregateRootEventConsumedRepository implements KafkaAggregateRootEventConsumedRepository {

    private static final String POSTGRESQL_DDL_FILE = "/sql/consumed-events-postgresql.ddl";

    private final AgroalDataSource consumedEventsDataSource;

    public AgroalDataSourcePostgreSqlAggregateRootEventConsumedRepository(@DataSource("consumed-events") final AgroalDataSource consumedEventsDataSource) {
        this.consumedEventsDataSource = Objects.requireNonNull(consumedEventsDataSource);
    }

    @PostConstruct
    public void initTables() {
        final InputStream ddlResource = this.getClass().getResourceAsStream(POSTGRESQL_DDL_FILE);
        try (final Scanner scanner = new Scanner(ddlResource).useDelimiter("!!");
             final Connection con = consumedEventsDataSource.getConnection();
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
    @Transactional
    public void addEventConsumerConsumed(final AggregateRootEventId aggregateRootEventId, final Class consumerClass, final LocalDateTime consumedAt,
                                         final KafkaInfrastructureMetadata kafkaInfrastructureMetadata, final String gitCommitId) {
        // Upsert EVENT_CONSUMED
        try (final Connection connection = consumedEventsDataSource.getConnection();
             final PreparedStatement upsertEventConsumedPreparedStatement = connection.prepareStatement("INSERT INTO CONSUMED_EVENT (aggregaterootid, aggregateroottype, version, consumed, consumedat, kafkapartition, kafkatopic, kafkaoffset) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON CONFLICT ON CONSTRAINT eventconsumed_pkey DO NOTHING")) {
            upsertEventConsumedPreparedStatement.setString(1, aggregateRootEventId.aggregateRootId().aggregateRootId());
            upsertEventConsumedPreparedStatement.setString(2, aggregateRootEventId.aggregateRootId().aggregateRootType());
            upsertEventConsumedPreparedStatement.setLong(3, aggregateRootEventId.version());
            upsertEventConsumedPreparedStatement.setBoolean(4, Boolean.FALSE);
            upsertEventConsumedPreparedStatement.setObject(5, consumedAt);
            upsertEventConsumedPreparedStatement.setInt(6, kafkaInfrastructureMetadata.partition());
            upsertEventConsumedPreparedStatement.setString(7, kafkaInfrastructureMetadata.topic());
            upsertEventConsumedPreparedStatement.setLong(8, kafkaInfrastructureMetadata.offset());
            upsertEventConsumedPreparedStatement.executeUpdate();
            // Add CONSUMED_EVENT_CONSUMER
            try (final PreparedStatement addEventConsumerConsumedPreparedStatement = connection.prepareStatement(
                    "INSERT INTO CONSUMED_EVENT_CONSUMER (aggregaterootid, aggregateroottype, version, consumerclassname, consumedat, gitcommitid) " +
                            "VALUES (?, ?, ?, ?, ?, ?)")) {
                addEventConsumerConsumedPreparedStatement.setString(1, aggregateRootEventId.aggregateRootId().aggregateRootId());
                addEventConsumerConsumedPreparedStatement.setString(2, aggregateRootEventId.aggregateRootId().aggregateRootType());
                addEventConsumerConsumedPreparedStatement.setLong(3, aggregateRootEventId.version());
                addEventConsumerConsumedPreparedStatement.setString(4, consumerClass.getName());
                addEventConsumerConsumedPreparedStatement.setObject(5, consumedAt);
                addEventConsumerConsumedPreparedStatement.setString(6, gitCommitId);
                addEventConsumerConsumedPreparedStatement.executeUpdate();
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void markEventAsConsumed(final AggregateRootEventId aggregateRootEventId, final LocalDateTime consumedAt, final KafkaInfrastructureMetadata kafkaInfrastructureMetadata) {
        try (final Connection connection = consumedEventsDataSource.getConnection();
             final PreparedStatement markEventAsConsumedPreparedStatement = connection.prepareStatement("INSERT INTO CONSUMED_EVENT (aggregaterootid, aggregateroottype, version, consumed, consumedat, kafkapartition, kafkatopic, kafkaoffset) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON CONFLICT ON CONSTRAINT eventconsumed_pkey DO UPDATE SET consumed = EXCLUDED.consumed")) {
            markEventAsConsumedPreparedStatement.setString(1, aggregateRootEventId.aggregateRootId().aggregateRootId());
            markEventAsConsumedPreparedStatement.setString(2, aggregateRootEventId.aggregateRootId().aggregateRootType());
            markEventAsConsumedPreparedStatement.setLong(3, aggregateRootEventId.version());
            markEventAsConsumedPreparedStatement.setBoolean(4, Boolean.TRUE);
            markEventAsConsumedPreparedStatement.setObject(5, consumedAt);
            markEventAsConsumedPreparedStatement.setInt(6, kafkaInfrastructureMetadata.partition());
            markEventAsConsumedPreparedStatement.setString(7, kafkaInfrastructureMetadata.topic());
            markEventAsConsumedPreparedStatement.setLong(8, kafkaInfrastructureMetadata.offset());
            markEventAsConsumedPreparedStatement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public boolean hasFinishedConsumingEvent(final AggregateRootEventId aggregateRootEventId) {
        try (final Connection connection = consumedEventsDataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT consumed FROM CONSUMED_EVENT WHERE aggregaterootid = ? AND aggregateroottype = ? AND version = ?")) {
            preparedStatement.setString(1, aggregateRootEventId.aggregateRootId().aggregateRootId());
            preparedStatement.setString(2, aggregateRootEventId.aggregateRootId().aggregateRootType());
            preparedStatement.setLong(3, aggregateRootEventId.version());
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("consumed");
                }
                return false;
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public List<String> getConsumersHavingProcessedEvent(final AggregateRootEventId aggregateRootEventId) {
        try (final Connection connection = consumedEventsDataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT e.consumerclassname FROM CONSUMED_EVENT_CONSUMER e WHERE aggregaterootid = ? AND aggregateroottype = ? AND version = ?")) {
            preparedStatement.setString(1, aggregateRootEventId.aggregateRootId().aggregateRootId());
            preparedStatement.setString(2, aggregateRootEventId.aggregateRootId().aggregateRootType());
            preparedStatement.setLong(3, aggregateRootEventId.version());
            final List<String> consumersAlreadyProcessedEvent = new ArrayList<>();
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    consumersAlreadyProcessedEvent.add(resultSet.getString("consumerclassname"));
                }
                return consumersAlreadyProcessedEvent;
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
