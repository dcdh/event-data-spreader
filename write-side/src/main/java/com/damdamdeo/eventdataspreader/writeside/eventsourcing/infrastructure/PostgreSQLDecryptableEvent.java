package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.event.api.AggregateRootEventId;
import com.damdamdeo.eventdataspreader.eventsourcing.api.*;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.*;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEvent;
import org.apache.commons.lang3.Validate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public final class PostgreSQLDecryptableEvent implements DecryptableEvent {

    private final PostgreSQLAggregateRootEventId postgreSQLEventId;

    private final LocalDateTime creationDate;

    private final String eventType;

    private final String eventMetaData;

    private final String eventPayload;

    public PostgreSQLDecryptableEvent(final ResultSet resultSet) throws SQLException {
        this.postgreSQLEventId = new PostgreSQLAggregateRootEventId(resultSet);
        this.creationDate = resultSet.getObject("creationdate", LocalDateTime.class);
        this.eventType = resultSet.getString("eventtype");
        this.eventMetaData = resultSet.getString("eventmetadata");
        this.eventPayload = resultSet.getString("eventpayload");
    }

    private PostgreSQLDecryptableEvent(final EncryptedEventBuilder builder,
                                       final Optional<EncryptedEventSecret> encryptedEventSecret,
                                       final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer,
                                       final AggregateRootEventMetadataDeSerializer aggregateRootEventMetadataDeSerializer) {
        this.postgreSQLEventId = new PostgreSQLAggregateRootEventId(builder.aggregateRootEventId);
        this.eventType = builder.eventType;
        this.creationDate = builder.creationDate;
        this.eventPayload = aggregateRootEventPayloadDeSerializer.serialize(encryptedEventSecret, builder.aggregateRootEventPayload);
        this.eventMetaData = aggregateRootEventMetadataDeSerializer.serialize(encryptedEventSecret, builder.aggregateRootEventMetadata);
    }

    public PreparedStatement insertStatement(final Connection con) throws SQLException {
        final PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO EVENT (aggregaterootid, aggregateroottype, version, creationdate, eventtype, eventmetadata, eventpayload) " +
                "VALUES (?, ?, ?, ?, ?, to_json(?::json), to_json(?::json))");
        preparedStatement.setString(1, postgreSQLEventId.aggregateRootId().aggregateRootId());
        preparedStatement.setString(2, postgreSQLEventId.aggregateRootId().aggregateRootType());
        preparedStatement.setLong(3, postgreSQLEventId.version());
        preparedStatement.setObject(4, creationDate);
        preparedStatement.setString(5, eventType);
        preparedStatement.setString(6, eventMetaData);
        preparedStatement.setString(7, eventPayload);
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
        private AggregateRootEventMetadata aggregateRootEventMetadata;

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

        public EncryptedEventBuilder withEventMetaData(final AggregateRootEventMetadata aggregateRootEventMetadata) {
            this.aggregateRootEventMetadata = aggregateRootEventMetadata;
            return this;
        }

        public PostgreSQLDecryptableEvent build(final Optional<EncryptedEventSecret> encryptedEventSecret,
                                                final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer,
                                                final AggregateRootEventMetadataDeSerializer aggregateRootEventMetadataDeSerializer) {
            Validate.notNull(aggregateRootEventId);
            Validate.notNull(eventType);
            Validate.notNull(creationDate);
            Validate.notNull(aggregateRootEventPayload);
            Validate.notNull(aggregateRootEventMetadata);
            Validate.notNull(encryptedEventSecret);
            Validate.notNull(aggregateRootEventPayloadDeSerializer);
            Validate.notNull(aggregateRootEventMetadataDeSerializer);
            Validate.validState(encryptedEventSecret.isPresent() ? encryptedEventSecret.get().aggregateRootId().equals(aggregateRootEventId.aggregateRootId().aggregateRootId()) : true);
            Validate.validState(encryptedEventSecret.isPresent() ? encryptedEventSecret.get().aggregateRootType().equals(aggregateRootEventId.aggregateRootId().aggregateRootType()) : true);
            return new PostgreSQLDecryptableEvent(this, encryptedEventSecret, aggregateRootEventPayloadDeSerializer, aggregateRootEventMetadataDeSerializer);
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
    public AggregateRootEventPayload eventPayload(final Optional<EncryptedEventSecret> encryptedEventSecret,
                                                  final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer) {
        Validate.validState(encryptedEventSecret.isPresent() ? encryptedEventSecret.get().aggregateRootId().equals(postgreSQLEventId.aggregateRootId().aggregateRootId()) : true);
        Validate.validState(encryptedEventSecret.isPresent() ? encryptedEventSecret.get().aggregateRootType().equals(postgreSQLEventId.aggregateRootId().aggregateRootType()) : true);
        return aggregateRootEventPayloadDeSerializer.deserialize(encryptedEventSecret, eventPayload);
    }

    @Override
    public AggregateRootEventMetadata eventMetaData(final Optional<EncryptedEventSecret> encryptedEventSecret,
                                                    final AggregateRootEventMetadataDeSerializer aggregateRootEventMetadataDeSerializer) {
        Validate.validState(encryptedEventSecret.isPresent() ? encryptedEventSecret.get().aggregateRootId().equals(postgreSQLEventId.aggregateRootId().aggregateRootId()) : true);
        Validate.validState(encryptedEventSecret.isPresent() ? encryptedEventSecret.get().aggregateRootType().equals(postgreSQLEventId.aggregateRootId().aggregateRootType()) : true);
        return aggregateRootEventMetadataDeSerializer.deserialize(encryptedEventSecret, eventMetaData);
    }

    public AggregateRootEvent toEvent(final Optional<EncryptedEventSecret> encryptedEventSecret,
                                      final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer,
                                      final AggregateRootEventMetadataDeSerializer aggregateRootEventMetadataDeSerializer) {
        Validate.notNull(encryptedEventSecret);
        Validate.notNull(aggregateRootEventPayloadDeSerializer);
        Validate.notNull(aggregateRootEventMetadataDeSerializer);
        Validate.validState(encryptedEventSecret.isPresent() ? encryptedEventSecret.get().aggregateRootId().equals(postgreSQLEventId.aggregateRootId().aggregateRootId()) : true);
        Validate.validState(encryptedEventSecret.isPresent() ? encryptedEventSecret.get().aggregateRootType().equals(postgreSQLEventId.aggregateRootId().aggregateRootType()) : true);
        return new AggregateRootEvent(this, encryptedEventSecret, aggregateRootEventPayloadDeSerializer, aggregateRootEventMetadataDeSerializer);
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
                Objects.equals(eventPayload, that.eventPayload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postgreSQLEventId, creationDate, eventType, eventMetaData, eventPayload);
    }

    @Override
    public String toString() {
        return "PostgreSQLDecryptableEvent{" +
                "postgreSQLEventId=" + postgreSQLEventId +
                ", creationDate=" + creationDate +
                ", eventType='" + eventType + '\'' +
                ", eventMetaData='" + eventMetaData + '\'' +
                ", eventPayload='" + eventPayload + '\'' +
                '}';
    }
}
