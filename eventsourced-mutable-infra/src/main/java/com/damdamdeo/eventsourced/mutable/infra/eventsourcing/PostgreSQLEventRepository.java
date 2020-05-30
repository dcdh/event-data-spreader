package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRootEvent;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.EventRepository;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.aggregaterootevent.AggregateRootEventMetadataDeSerializer;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.aggregaterootevent.AggregateRootEventPayloadDeSerializer;
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

// TODO fait trop de chose
// Je devrais uniquement stocker un event et non appliquer la serialization et la deserialization

@Startup
@ApplicationScoped
public class PostgreSQLEventRepository implements EventRepository {

    private static final String POSTGRESQL_DDL_FILE = "/sql/event-store-postgresql.ddl";

    final AgroalDataSource mutableDataSource;
    final Encryption encryption;
    final SecretStore secretStore;
    final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer;
    final AggregateRootEventMetadataDeSerializer aggregateRootEventMetadataDeSerializer;

    public PostgreSQLEventRepository(@DataSource("mutable") final AgroalDataSource mutableDataSource,
                                     final Encryption encryption,
                                     final SecretStore secretStore,
                                     final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer,
                                     final AggregateRootEventMetadataDeSerializer aggregateRootEventMetadataDeSerializer) {
        this.mutableDataSource = Objects.requireNonNull(mutableDataSource);
        this.encryption = encryption;
        this.secretStore = Objects.requireNonNull(secretStore);
        this.aggregateRootEventPayloadDeSerializer = Objects.requireNonNull(aggregateRootEventPayloadDeSerializer);
        this.aggregateRootEventMetadataDeSerializer = Objects.requireNonNull(aggregateRootEventMetadataDeSerializer);
    }

    @PostConstruct
    public void initTables() {
        final InputStream ddlResource = this.getClass().getResourceAsStream(POSTGRESQL_DDL_FILE);
        try (final Scanner scanner = new Scanner(ddlResource).useDelimiter("!!");
             final Connection con = mutableDataSource.getConnection();
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
        final AggregateRootId aggregateRootId = aggregateRootEvents.get(0).aggregateRootId();
        Validate.validState(aggregateRootEvents.stream().allMatch(event -> aggregateRootId.equals(event.aggregateRootId())));
        final Optional<AggregateRootSecret> aggregateRootSecret = getAggregateRootSecret(aggregateRootEvents, encryption);
        final List<PostgreSQLDecryptableEvent> eventsToSave = aggregateRootEvents.stream()
                .map(event -> PostgreSQLDecryptableEvent.newEncryptedEventBuilder()
                        .withEventId(event.eventId())
                        .withEventType(event.eventType())
                        .withCreationDate(event.creationDate())
                        .withEventPayload(event.eventPayload())
                        .withEventMetaData(event.eventMetaData())
                        .build(aggregateRootSecret, aggregateRootEventPayloadDeSerializer, aggregateRootEventMetadataDeSerializer))
                .collect(Collectors.toList());
        try (final Connection connection = mutableDataSource.getConnection()) {
            for (final PostgreSQLDecryptableEvent postgreSQLDecryptableEvent : eventsToSave) {
                try (final PreparedStatement preparedStatement = postgreSQLDecryptableEvent.insertStatement(connection)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<AggregateRootSecret> getAggregateRootSecret(final List<AggregateRootEvent> aggregateRootEvents,
                                                                 final Encryption encryption) {
        final AggregateRootId aggregateRootId = aggregateRootEvents.get(0).aggregateRootId();
        if (aggregateRootEvents.get(0).version() == 0L) {
            final String newSecretToStore = encryption.generateNewSecret();
            final AggregateRootSecret newAggregateRootSecret = secretStore.store(aggregateRootId.aggregateRootType(),
                    aggregateRootId.aggregateRootId(),
                    newSecretToStore);
            return Optional.of(newAggregateRootSecret);
        }
        return secretStore.read(aggregateRootId.aggregateRootType(),
                aggregateRootId.aggregateRootId());
    }

    @Transactional
    @Override
    public List<AggregateRootEvent> loadOrderByVersionASC(final String aggregateRootId, final String aggregateRootType) {
        final Optional<AggregateRootSecret> aggregateRootSecret = secretStore.read(aggregateRootType,aggregateRootId);
        try (final Connection connection = mutableDataSource.getConnection();
             final PreparedStatement stmt = connection.prepareStatement("SELECT * FROM EVENT e WHERE e.aggregaterootid = ? AND e.aggregateroottype = ? ORDER BY e.version ASC")) {
            stmt.setString(1, aggregateRootId);
            stmt.setString(2, aggregateRootType);
            final List<AggregateRootEvent> aggregateRootEvents = new ArrayList<>();
            // TODO I should get the number of event to initialize list size. However, a lot of copies will be made in memory on large result set.
            try (final ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    aggregateRootEvents.add(new PostgreSQLDecryptableEvent(resultSet).toEvent(aggregateRootSecret, aggregateRootEventPayloadDeSerializer, aggregateRootEventMetadataDeSerializer));
                }
            }
            return aggregateRootEvents;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
