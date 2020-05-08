package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumedRepository;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.KafkaSource;
import com.damdamdeo.eventdataspreader.event.api.EventId;
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
public class AgroalDataSourcePostgreSqlEventConsumedRepository implements EventConsumedRepository {

    private static final String POSTGRESQL_DDL_FILE = "/sql/consumed-events-postgresql.ddl";

    private final AgroalDataSource consumedEventsDataSource;

    public AgroalDataSourcePostgreSqlEventConsumedRepository(@DataSource("consumed-events") final AgroalDataSource consumedEventsDataSource) {
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
    // FIXME FCK le nom n'est pas bon : startConsumingEvent !!!
    public void addEventConsumerConsumed(final EventId eventId, final Class consumerClass, final LocalDateTime consumedAt, final KafkaSource kafkaSource, final String gitCommitId) {
        // Upsert EVENT_CONSUMED
        try (final Connection connection = consumedEventsDataSource.getConnection();
             final PreparedStatement upsertEventConsumedPreparedStatement = connection.prepareStatement("INSERT INTO CONSUMED_EVENT (aggregaterootid, aggregateroottype, version, consumed, consumedat, kafkapartition, kafkatopic, kafkaoffset) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON CONFLICT ON CONSTRAINT eventconsumed_pkey DO NOTHING")) {
            upsertEventConsumedPreparedStatement.setString(1, eventId.aggregateRootId());
            upsertEventConsumedPreparedStatement.setString(2, eventId.aggregateRootType());
            upsertEventConsumedPreparedStatement.setLong(3, eventId.version());
            upsertEventConsumedPreparedStatement.setBoolean(4, Boolean.FALSE);
            upsertEventConsumedPreparedStatement.setObject(5, consumedAt);
            upsertEventConsumedPreparedStatement.setInt(6, kafkaSource.partition());
            upsertEventConsumedPreparedStatement.setString(7, kafkaSource.topic());
            upsertEventConsumedPreparedStatement.setLong(8, kafkaSource.offset());
            upsertEventConsumedPreparedStatement.executeUpdate();
            // Add CONSUMED_EVENT_CONSUMER
            try (final PreparedStatement addEventConsumerConsumedPreparedStatement = connection.prepareStatement(
                    "INSERT INTO CONSUMED_EVENT_CONSUMER (aggregaterootid, aggregateroottype, version, consumerclassname, consumedat, gitcommitid) " +
                            "VALUES (?, ?, ?, ?, ?, ?)")) {
                addEventConsumerConsumedPreparedStatement.setString(1, eventId.aggregateRootId());
                addEventConsumerConsumedPreparedStatement.setString(2, eventId.aggregateRootType());
                addEventConsumerConsumedPreparedStatement.setLong(3, eventId.version());
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
    public void markEventAsConsumed(final EventId eventId, final LocalDateTime consumedAt, final KafkaSource kafkaSource) {
        try (final Connection connection = consumedEventsDataSource.getConnection();
             final PreparedStatement markEventAsConsumedPreparedStatement = connection.prepareStatement("INSERT INTO CONSUMED_EVENT (aggregaterootid, aggregateroottype, version, consumed, consumedat, kafkapartition, kafkatopic, kafkaoffset) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON CONFLICT ON CONSTRAINT eventconsumed_pkey DO UPDATE SET consumed = EXCLUDED.consumed")) {
            markEventAsConsumedPreparedStatement.setString(1, eventId.aggregateRootId());
            markEventAsConsumedPreparedStatement.setString(2, eventId.aggregateRootType());
            markEventAsConsumedPreparedStatement.setLong(3, eventId.version());
            markEventAsConsumedPreparedStatement.setBoolean(4, Boolean.TRUE);
            markEventAsConsumedPreparedStatement.setObject(5, consumedAt);
            markEventAsConsumedPreparedStatement.setInt(6, kafkaSource.partition());
            markEventAsConsumedPreparedStatement.setString(7, kafkaSource.topic());
            markEventAsConsumedPreparedStatement.setLong(8, kafkaSource.offset());
            markEventAsConsumedPreparedStatement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public boolean hasFinishedConsumingEvent(final EventId eventId) {
        try (final Connection connection = consumedEventsDataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT consumed FROM CONSUMED_EVENT WHERE aggregaterootid = ? AND aggregateroottype = ? AND version = ?")) {
            preparedStatement.setString(1, eventId.aggregateRootId());
            preparedStatement.setString(2, eventId.aggregateRootType());
            preparedStatement.setLong(3, eventId.version());
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
    public List<String> getConsumersHavingProcessedEvent(final EventId eventId) {
        try (final Connection connection = consumedEventsDataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT e.consumerclassname FROM CONSUMED_EVENT_CONSUMER e WHERE aggregaterootid = ? AND aggregateroottype = ? AND version = ?")) {
            preparedStatement.setString(1, eventId.aggregateRootId());
            preparedStatement.setString(2, eventId.aggregateRootType());
            preparedStatement.setLong(3, eventId.version());
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
