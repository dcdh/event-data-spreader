package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.*;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventMetadataDeSerializer;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventPayloadsDeSerializer;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootMaterializedStatesSerializer;
import org.apache.commons.lang3.Validate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;

public final class PostgreSQLDecryptableEvent implements DecryptableEvent {

    private final PostgreSQLAggregateRootEventId postgreSQLEventId;

    private final LocalDateTime creationDate;

    private final String eventType;

    private final String eventMetaData;

    private final String eventPayload;

    private final String materializedState;

    public PostgreSQLDecryptableEvent(final ResultSet resultSet) throws SQLException {
        this.postgreSQLEventId = new PostgreSQLAggregateRootEventId(resultSet);
        this.creationDate = resultSet.getObject("creationdate", LocalDateTime.class);
        this.eventType = resultSet.getString("eventtype");
        this.eventMetaData = resultSet.getString("eventmetadata");
        this.eventPayload = resultSet.getString("eventpayload");
        this.materializedState = resultSet.getString("materializedstate");
    }

    private PostgreSQLDecryptableEvent(final EncryptedEventBuilder builder,
                                       final AggregateRootEventPayloadsDeSerializer aggregateRootEventPayloadsDeSerializer,
                                       final AggregateRootEventMetadataDeSerializer aggregateRootEventMetadataDeSerializer,
                                       final AggregateRootMaterializedStatesSerializer aggregateRootMaterializedStatesSerializer,
                                       final Secret secret,
                                       final boolean shouldEncryptAggregateRootMaterializedStates) {
        this.postgreSQLEventId = new PostgreSQLAggregateRootEventId(builder.aggregateRootEventId);
        this.eventType = builder.eventType;
        this.creationDate = builder.creationDate;
        this.eventMetaData = aggregateRootEventMetadataDeSerializer.serialize();
        this.eventPayload = aggregateRootEventPayloadsDeSerializer.serialize(builder.aggregateRootEventId.aggregateRootId(),
                builder.eventType,
                builder.aggregateRootEventPayload);
        this.materializedState = aggregateRootMaterializedStatesSerializer.serialize(builder.aggregateRoot, secret, shouldEncryptAggregateRootMaterializedStates);
    }

    public PreparedStatement insertStatement(final Connection con, final GitCommitProvider gitCommitProvider) throws SQLException {
        final PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO EVENT (aggregaterootid, aggregateroottype, version, creationdate, eventtype, eventmetadata, eventpayload, materializedstate, gitcommitid) " +
                "VALUES (?, ?, ?, ?, ?, to_json(?::json), to_json(?::json), to_json(?::json), ?)");
        preparedStatement.setString(1, postgreSQLEventId.aggregateRootId().aggregateRootId());
        preparedStatement.setString(2, postgreSQLEventId.aggregateRootId().aggregateRootType());
        preparedStatement.setLong(3, postgreSQLEventId.version());
        preparedStatement.setObject(4, creationDate);
        preparedStatement.setString(5, eventType);
        preparedStatement.setString(6, eventMetaData);
        preparedStatement.setString(7, eventPayload);
        preparedStatement.setString(8, materializedState);
        preparedStatement.setString(9, gitCommitProvider.gitCommitId());
        return preparedStatement;
    }

    public static EncryptedEventBuilder newEncryptedEventBuilder() {
        return new EncryptedEventBuilder();
    }

    public static class EncryptedEventBuilder {
        private AggregateRootEventId aggregateRootEventId;
        private String eventType;
        private LocalDateTime creationDate;
        private AggregateRootEventPayload aggregateRootEventPayload;
        private AggregateRoot aggregateRoot;

        public EncryptedEventBuilder withEventId(final AggregateRootEventId aggregateRootEventId) {
            this.aggregateRootEventId = aggregateRootEventId;
            return this;
        }

        public EncryptedEventBuilder withEventType(final String eventType) {
            this.eventType = eventType;
            return this;
        }

        public EncryptedEventBuilder withCreationDate(final LocalDateTime creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public EncryptedEventBuilder withEventPayload(final AggregateRootEventPayload aggregateRootEventPayload) {
            this.aggregateRootEventPayload = aggregateRootEventPayload;
            return this;
        }

        public EncryptedEventBuilder withAggregateRoot(final AggregateRoot aggregateRoot) {
            this.aggregateRoot = aggregateRoot;
            return this;
        }

        public PostgreSQLDecryptableEvent build(final AggregateRootEventPayloadsDeSerializer aggregateRootEventPayloadsDeSerializer,
                                                final AggregateRootEventMetadataDeSerializer aggregateRootEventMetadataDeSerializer,
                                                final AggregateRootMaterializedStatesSerializer aggregateRootMaterializedStatesSerializer,
                                                final Secret secret,
                                                final boolean shouldEncryptAggregateRootMaterializedStates) {
            Validate.notNull(aggregateRootEventId);
            Validate.notNull(eventType);
            Validate.notNull(creationDate);
            Validate.notNull(aggregateRootEventPayload);
            Validate.notNull(aggregateRoot);
            Validate.validState(aggregateRootEventId.aggregateRootId().aggregateRootId().equals(aggregateRoot.aggregateRootId().aggregateRootId()));
            Validate.validState(aggregateRootEventId.aggregateRootId().aggregateRootType().equals(aggregateRoot.aggregateRootId().aggregateRootType()));
            Validate.notNull(aggregateRootEventPayloadsDeSerializer);
            Validate.notNull(aggregateRootMaterializedStatesSerializer);
            return new PostgreSQLDecryptableEvent(this, aggregateRootEventPayloadsDeSerializer, aggregateRootEventMetadataDeSerializer,
                    aggregateRootMaterializedStatesSerializer, secret, shouldEncryptAggregateRootMaterializedStates);
        }

    }

    @Override
    public AggregateRootEventId eventId() {
        return postgreSQLEventId;
    }

    @Override
    public String eventType() {
        return eventType;
    }

    @Override
    public LocalDateTime creationDate() {
        return creationDate;
    }

    @Override
    public AggregateRootEventPayload eventPayload(final AggregateRootEventPayloadsDeSerializer aggregateRootEventPayloadsDeSerializer)
            throws UnsupportedAggregateRootEventPayload {
        return aggregateRootEventPayloadsDeSerializer.deserialize(postgreSQLEventId.aggregateRootType(), eventType, eventPayload);
    }

    public AggregateRootEvent toEvent(final AggregateRootEventPayloadsDeSerializer aggregateRootEventPayloadsDeSerializer) {
        Validate.notNull(aggregateRootEventPayloadsDeSerializer);
        return new AggregateRootEvent(this, aggregateRootEventPayloadsDeSerializer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostgreSQLDecryptableEvent that = (PostgreSQLDecryptableEvent) o;
        return Objects.equals(postgreSQLEventId, that.postgreSQLEventId) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(eventType, that.eventType) &&
                Objects.equals(eventMetaData, that.eventMetaData) &&
                Objects.equals(eventPayload, that.eventPayload) &&
                Objects.equals(materializedState, that.materializedState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postgreSQLEventId, creationDate, eventType, eventMetaData, eventPayload, materializedState);
    }

    @Override
    public String toString() {
        return "PostgreSQLDecryptableEvent{" +
                "postgreSQLEventId=" + postgreSQLEventId +
                ", creationDate=" + creationDate +
                ", eventType='" + eventType + '\'' +
                ", eventMetaData='" + eventMetaData + '\'' +
                ", eventPayload='" + eventPayload + '\'' +
                ", materializedState='" + materializedState + '\'' +
                '}';
    }
}
