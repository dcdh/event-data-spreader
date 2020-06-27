package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.*;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventMetadataDeSerializer;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventPayloadDeSerializer;
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

// TODO fait trop de chose
// Je devrais uniquement stocker un event et non appliquer la serialization et la deserialization

@Startup
@ApplicationScoped
public class PostgreSQLEventRepository implements EventRepository {

    private static final String POSTGRESQL_DDL_FILE = "/sql/event-store-postgresql.ddl";

    final AgroalDataSource mutableDataSource;
    final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer;
    final AggregateRootEventMetadataDeSerializer aggregateRootEventMetadataDeSerializer;
    final AggregateRootMaterializedStateSerializer aggregateRootMaterializedStateSerializer;
    final GitCommitProvider gitCommitProvider;

    public PostgreSQLEventRepository(@DataSource("mutable") final AgroalDataSource mutableDataSource,
                                     final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer,
                                     final AggregateRootEventMetadataDeSerializer aggregateRootEventMetadataDeSerializer,
                                     final AggregateRootMaterializedStateSerializer aggregateRootMaterializedStateSerializer,
                                     final GitCommitProvider gitCommitProvider) {
        this.mutableDataSource = Objects.requireNonNull(mutableDataSource);
        this.aggregateRootEventPayloadDeSerializer = Objects.requireNonNull(aggregateRootEventPayloadDeSerializer);
        this.aggregateRootEventMetadataDeSerializer = Objects.requireNonNull(aggregateRootEventMetadataDeSerializer);
        this.aggregateRootMaterializedStateSerializer = Objects.requireNonNull(aggregateRootMaterializedStateSerializer);
        this.gitCommitProvider = Objects.requireNonNull(gitCommitProvider);
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
    public void save(final AggregateRootEvent aggregateRootEvent, final AggregateRoot aggregateRoot, final Secret secret) {
        Validate.notNull(aggregateRootEvent);
        Validate.notNull(aggregateRoot);
        Validate.notNull(secret);
        Validate.validState(aggregateRootEvent.aggregateRootId().aggregateRootId().equals(aggregateRoot.aggregateRootId().aggregateRootId()));
        Validate.validState(aggregateRootEvent.aggregateRootId().aggregateRootType().equals(aggregateRoot.aggregateRootId().aggregateRootType()));
        Validate.validState(aggregateRootEvent.version().equals(aggregateRoot.version()));
        final PostgreSQLDecryptableEvent eventToSave = PostgreSQLDecryptableEvent.newEncryptedEventBuilder()
                .withEventId(aggregateRootEvent.eventId())
                .withEventType(aggregateRootEvent.eventType())
                .withCreationDate(aggregateRootEvent.creationDate())
                .withEventPayload(aggregateRootEvent.eventPayload())
                .withEventMetaData(aggregateRootEvent.eventMetaData())
                .withAggregateRoot(aggregateRoot)
                .build(secret, aggregateRootEventPayloadDeSerializer, aggregateRootEventMetadataDeSerializer, aggregateRootMaterializedStateSerializer);
        try (final Connection connection = mutableDataSource.getConnection();
             final PreparedStatement preparedStatement = eventToSave.insertStatement(connection, gitCommitProvider)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    @Override
    public List<AggregateRootEvent> loadOrderByVersionASC(final String aggregateRootId, final String aggregateRootType, final Secret secret) {
        try (final Connection connection = mutableDataSource.getConnection();
             final PreparedStatement stmt = connection.prepareStatement("SELECT * FROM EVENT e WHERE e.aggregaterootid = ? AND e.aggregateroottype = ? ORDER BY e.version ASC")) {
            stmt.setString(1, aggregateRootId);
            stmt.setString(2, aggregateRootType);
            final List<AggregateRootEvent> aggregateRootEvents = new ArrayList<>();
            // TODO I should get the number of event to initialize list size. However, a lot of copies will be made in memory on large result set.
            try (final ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    aggregateRootEvents.add(new PostgreSQLDecryptableEvent(resultSet).toEvent(secret, aggregateRootEventPayloadDeSerializer, aggregateRootEventMetadataDeSerializer));
                }
            }
            return aggregateRootEvents;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AggregateRootEvent> loadOrderByVersionASC(final String aggregateRootId, final String aggregateRootType, final Secret secret, final Long version) {
        try (final Connection connection = mutableDataSource.getConnection();
             final PreparedStatement stmt = connection.prepareStatement("SELECT * FROM EVENT e WHERE e.aggregaterootid = ? AND e.aggregateroottype = ? AND e.version <= ? ORDER BY e.version ASC")) {
            stmt.setString(1, aggregateRootId);
            stmt.setString(2, aggregateRootType);
            stmt.setLong(3, version);
            final List<AggregateRootEvent> aggregateRootEvents = new ArrayList<>(version.intValue());
            try (final ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    aggregateRootEvents.add(new PostgreSQLDecryptableEvent(resultSet).toEvent(secret, aggregateRootEventPayloadDeSerializer, aggregateRootEventMetadataDeSerializer));
                }
            }
            return aggregateRootEvents;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
