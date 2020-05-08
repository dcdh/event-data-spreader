package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.event.api.EventMetadataDeserializer;
import com.damdamdeo.eventdataspreader.event.api.EventMetadataSerializer;
import com.damdamdeo.eventdataspreader.eventsourcing.api.*;
import com.damdamdeo.eventdataspreader.eventsourcing.infrastructure.AESEncryption;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayloadDeSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.Event;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventRepository;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.runtime.Startup;
import org.apache.commons.lang3.Validate;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Startup
@ApplicationScoped
public class AgroalDataSourcePostgreSqlEventRepository implements EventRepository {

    private static final String POSTGRESQL_DDL_FILE = "/sql/event-store-postgresql.ddl";

    final AgroalDataSource aggregateRootProjectionEventStoreDataSource;
    final Encryption encryption;
    final SecretStore secretStore;
    final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer;
    final EventMetadataSerializer eventMetadataSerializer;
    final EventMetadataDeserializer eventMetadataDeserializer;

    public AgroalDataSourcePostgreSqlEventRepository(@DataSource("aggregate-root-projection-event-store") final AgroalDataSource aggregateRootProjectionEventStoreDataSource,
                                                     final SecretStore secretStore,
                                                     final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer,
                                                     final EventMetadataSerializer eventMetadataSerializer,
                                                     final EventMetadataDeserializer eventMetadataDeserializer) {
        this.aggregateRootProjectionEventStoreDataSource = Objects.requireNonNull(aggregateRootProjectionEventStoreDataSource);
        this.encryption = new AESEncryption();
        this.secretStore = secretStore;
        this.aggregateRootEventPayloadDeSerializer = aggregateRootEventPayloadDeSerializer;
        this.eventMetadataSerializer = eventMetadataSerializer;
        this.eventMetadataDeserializer = eventMetadataDeserializer;
    }

    @PostConstruct
    public void initTables() {
        final InputStream ddlResource = this.getClass().getResourceAsStream(POSTGRESQL_DDL_FILE);
        try (final Scanner scanner = new Scanner(ddlResource).useDelimiter("!!");
             final Connection con = aggregateRootProjectionEventStoreDataSource.getConnection();
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

    @Transactional
    @Override
    public void save(final List<Event> events) {
        Validate.validState(events.size() > 0);
        final String aggregateRootId = events.get(0).aggregateRootId();
        final String aggregateRootType = events.get(0).aggregateRootType();
        Validate.validState(events.stream().allMatch(event -> aggregateRootId.equals(event.aggregateRootId())
                && aggregateRootType.equals(event.aggregateRootType())));
        final Optional<EncryptedEventSecret> encryptedEventSecret = getEncryptedEventKey(events, encryption);
        final List<PostgreSQLDecryptableEvent> eventsToSave = events.stream()
                .map(event -> PostgreSQLDecryptableEvent.newEncryptedEventBuilder()
                        .withEventId(event.eventId())
                        .withEventType(event.eventType())
                        .withCreationDate(event.creationDate())
                        .withEventPayload(event.eventPayload())
                        .withEventMetaData(event.eventMetaData())
                        .build(encryptedEventSecret, aggregateRootEventPayloadDeSerializer, eventMetadataSerializer))
                .collect(Collectors.toList());
        try (final Connection connection = aggregateRootProjectionEventStoreDataSource.getConnection()) {
            for (final PostgreSQLDecryptableEvent postgreSQLDecryptableEvent : eventsToSave) {
                try (final PreparedStatement preparedStatement = postgreSQLDecryptableEvent.insertStatement(connection)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<EncryptedEventSecret> getEncryptedEventKey(final List<Event> events,
                                                                final Encryption encryption) {
        final String aggregateRootType = events.get(0).aggregateRootType();
        final String aggregateRootId = events.get(0).aggregateRootId();

        if (events.get(0).version() == 0L) {
            final String newSecretToStore = encryption.generateNewSecret();
            final EncryptedEventSecret newEncryptedEventSecret = secretStore.store(aggregateRootType, aggregateRootId, newSecretToStore);
            return Optional.of(newEncryptedEventSecret);
        }
        return secretStore.read(aggregateRootType, aggregateRootId);
    }

    @Transactional
    @Override
    public List<Event> loadOrderByVersionASC(final String aggregateRootId, final String aggregateRootType) {
        final Optional<EncryptedEventSecret> encryptedEventSecret = secretStore.read(aggregateRootType,aggregateRootId);
        try (final Connection connection = aggregateRootProjectionEventStoreDataSource.getConnection();
             final PreparedStatement stmt = connection.prepareStatement("SELECT * FROM EVENT e WHERE e.aggregaterootid = ? AND e.aggregateroottype = ? ORDER BY e.version ASC")) {
            stmt.setString(1, aggregateRootId);
            stmt.setString(2, aggregateRootType);
            final List<Event> events = new ArrayList<>();
            // TODO I should get the number of event to initialize list size. However, a lot of copies will be made in memory on large result set.
            try (final ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    events.add(new PostgreSQLDecryptableEvent(resultSet).toEvent(encryptedEventSecret, aggregateRootEventPayloadDeSerializer, eventMetadataDeserializer));
                }
            }
            return events;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
