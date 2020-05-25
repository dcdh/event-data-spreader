package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.eventsourcing.api.*;
import com.damdamdeo.eventdataspreader.eventsourcing.infrastructure.AESEncryption;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventMetadataDeSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayloadDeSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEvent;
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
public class PostgreSQLEventRepository implements EventRepository {

    private static final String POSTGRESQL_DDL_FILE = "/sql/event-store-postgresql.ddl";

    final AgroalDataSource aggregateRootMaterializedStateDataSource;
    final Encryption encryption;
    final SecretStore secretStore;
    final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer;
    final AggregateRootEventMetadataDeSerializer aggregateRootEventMetadataDeSerializer;

    public PostgreSQLEventRepository(@DataSource("aggregate-root-materialized-state") final AgroalDataSource aggregateRootMaterializedStateDataSource,
                                     final SecretStore secretStore,
                                     final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer,
                                     final AggregateRootEventMetadataDeSerializer aggregateRootEventMetadataDeSerializer) {
        this.aggregateRootMaterializedStateDataSource = Objects.requireNonNull(aggregateRootMaterializedStateDataSource);
        this.encryption = new AESEncryption();
        this.secretStore = Objects.requireNonNull(secretStore);
        this.aggregateRootEventPayloadDeSerializer = Objects.requireNonNull(aggregateRootEventPayloadDeSerializer);
        this.aggregateRootEventMetadataDeSerializer = Objects.requireNonNull(aggregateRootEventMetadataDeSerializer);
    }

    @PostConstruct
    public void initTables() {
        final InputStream ddlResource = this.getClass().getResourceAsStream(POSTGRESQL_DDL_FILE);
        try (final Scanner scanner = new Scanner(ddlResource).useDelimiter("!!");
             final Connection con = aggregateRootMaterializedStateDataSource.getConnection();
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
    public void save(final List<AggregateRootEvent> aggregateRootEvents) {
        Validate.validState(aggregateRootEvents.size() > 0);
        final String aggregateRootId = aggregateRootEvents.get(0).aggregateRootId();
        final String aggregateRootType = aggregateRootEvents.get(0).aggregateRootType();
        Validate.validState(aggregateRootEvents.stream().allMatch(event -> aggregateRootId.equals(event.aggregateRootId())
                && aggregateRootType.equals(event.aggregateRootType())));
        final Optional<EncryptedEventSecret> encryptedEventSecret = getEncryptedEventKey(aggregateRootEvents, encryption);
        final List<PostgreSQLDecryptableEvent> eventsToSave = aggregateRootEvents.stream()
                .map(event -> PostgreSQLDecryptableEvent.newEncryptedEventBuilder()
                        .withEventId(event.eventId())
                        .withEventType(event.eventType())
                        .withCreationDate(event.creationDate())
                        .withEventPayload(event.eventPayload())
                        .withEventMetaData(event.eventMetaData())
                        .build(encryptedEventSecret, aggregateRootEventPayloadDeSerializer, aggregateRootEventMetadataDeSerializer))
                .collect(Collectors.toList());
        try (final Connection connection = aggregateRootMaterializedStateDataSource.getConnection()) {
            for (final PostgreSQLDecryptableEvent postgreSQLDecryptableEvent : eventsToSave) {
                try (final PreparedStatement preparedStatement = postgreSQLDecryptableEvent.insertStatement(connection)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<EncryptedEventSecret> getEncryptedEventKey(final List<AggregateRootEvent> aggregateRootEvents,
                                                                final Encryption encryption) {
        final String aggregateRootType = aggregateRootEvents.get(0).aggregateRootType();
        final String aggregateRootId = aggregateRootEvents.get(0).aggregateRootId();

        if (aggregateRootEvents.get(0).version() == 0L) {
            final String newSecretToStore = encryption.generateNewSecret();
            final EncryptedEventSecret newEncryptedEventSecret = secretStore.store(aggregateRootType, aggregateRootId, newSecretToStore);
            return Optional.of(newEncryptedEventSecret);
        }
        return secretStore.read(aggregateRootType, aggregateRootId);
    }

    @Transactional
    @Override
    public List<AggregateRootEvent> loadOrderByVersionASC(final String aggregateRootId, final String aggregateRootType) {
        final Optional<EncryptedEventSecret> encryptedEventSecret = secretStore.read(aggregateRootType,aggregateRootId);
        try (final Connection connection = aggregateRootMaterializedStateDataSource.getConnection();
             final PreparedStatement stmt = connection.prepareStatement("SELECT * FROM EVENT e WHERE e.aggregaterootid = ? AND e.aggregateroottype = ? ORDER BY e.version ASC")) {
            stmt.setString(1, aggregateRootId);
            stmt.setString(2, aggregateRootType);
            final List<AggregateRootEvent> aggregateRootEvents = new ArrayList<>();
            // TODO I should get the number of event to initialize list size. However, a lot of copies will be made in memory on large result set.
            try (final ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    aggregateRootEvents.add(new PostgreSQLDecryptableEvent(resultSet).toEvent(encryptedEventSecret, aggregateRootEventPayloadDeSerializer, aggregateRootEventMetadataDeSerializer));
                }
            }
            return aggregateRootEvents;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
