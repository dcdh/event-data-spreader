package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.event.api.EventId;
import com.damdamdeo.eventdataspreader.event.api.EventMetadata;
import com.damdamdeo.eventdataspreader.event.api.EventMetadataDeserializer;
import com.damdamdeo.eventdataspreader.event.api.EventMetadataSerializer;
import com.damdamdeo.eventdataspreader.eventsourcing.api.*;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.*;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.Event;
import org.apache.commons.lang3.Validate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public final class PostgreSQLDecryptableEvent implements DecryptableEvent {

    private final DefaultEventId defaultEventId;

    private final LocalDateTime creationDate;

    private final String eventType;

    private final String eventMetaData;

    private final String eventPayload;

    public PostgreSQLDecryptableEvent(final ResultSet resultSet) throws SQLException {
        this.defaultEventId = new DefaultEventId(
                resultSet.getString("aggregaterootid"),
                resultSet.getString("aggregateroottype"),
                resultSet.getLong("version")
        );
        this.creationDate = resultSet.getObject("creationdate", LocalDateTime.class);
        this.eventType = resultSet.getString("eventtype");
        this.eventMetaData = resultSet.getString("eventmetadata");
        this.eventPayload = resultSet.getString("eventpayload");
    }

    private PostgreSQLDecryptableEvent(final EncryptedEventBuilder builder,
                                       final Optional<EncryptedEventSecret> encryptedEventSecret,
                                       final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer,
                                       final EventMetadataSerializer eventMetadataSerializer) {
        this.defaultEventId = new DefaultEventId(builder.eventId);
        this.eventType = builder.eventType;
        this.creationDate = builder.creationDate;
        this.eventPayload = aggregateRootEventPayloadDeSerializer.serialize(encryptedEventSecret, builder.aggregateRootEventPayload);
        this.eventMetaData = eventMetadataSerializer.serialize(encryptedEventSecret, builder.eventMetaData);
    }

    public PreparedStatement insertStatement(final Connection con) throws SQLException {
        final PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO EVENT (aggregaterootid, aggregateroottype, version, creationdate, eventtype, eventmetadata, eventpayload) " +
                "VALUES (?, ?, ?, ?, ?, to_json(?::json), to_json(?::json))");
        preparedStatement.setString(1, defaultEventId.aggregateRootId());
        preparedStatement.setString(2, defaultEventId.aggregateRootType());
        preparedStatement.setLong(3, defaultEventId.version());
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
        private EventId eventId;
        private String eventType;
        private LocalDateTime creationDate;
        private AggregateRootEventPayload aggregateRootEventPayload;
        private EventMetadata eventMetaData;

        public EncryptedEventBuilder withEventId(final EventId eventId) {
            this.eventId = eventId;
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

        public EncryptedEventBuilder withEventMetaData(final EventMetadata eventMetaData) {
            this.eventMetaData = eventMetaData;
            return this;
        }

        public PostgreSQLDecryptableEvent build(final Optional<EncryptedEventSecret> encryptedEventSecret,
                                                final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer,
                                                final EventMetadataSerializer eventMetadataSerializer) {
            Validate.notNull(eventId);
            Validate.notNull(eventType);
            Validate.notNull(creationDate);
            Validate.notNull(aggregateRootEventPayload);
            Validate.notNull(eventMetaData);
            Validate.notNull(encryptedEventSecret);
            Validate.notNull(aggregateRootEventPayloadDeSerializer);
            Validate.notNull(eventMetadataSerializer);
            Validate.validState(encryptedEventSecret.isPresent() ? encryptedEventSecret.get().aggregateRootId().equals(eventId.aggregateRootId()) : true);
            Validate.validState(encryptedEventSecret.isPresent() ? encryptedEventSecret.get().aggregateRootType().equals(eventId.aggregateRootType()) : true);
            return new PostgreSQLDecryptableEvent(this, encryptedEventSecret, aggregateRootEventPayloadDeSerializer, eventMetadataSerializer);
        }

    }

    @Override
    public EventId eventId() {
        return defaultEventId;
    }

    @Override
    public String eventType() {
        return eventType;
    }

    @Override
    public LocalDateTime creationDate() {
        return creationDate;
    }

    public String aggregateRootId() {
        return defaultEventId.aggregateRootId();
    }

    public String aggregateRootType() {
        return defaultEventId.aggregateRootType();
    }

    public Long version() {
        return defaultEventId.version();
    }

    @Override
    public AggregateRootEventPayload eventPayload(final Optional<EncryptedEventSecret> encryptedEventSecret,
                                                  final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer) {
        Validate.validState(encryptedEventSecret.isPresent() ? encryptedEventSecret.get().aggregateRootId().equals(defaultEventId.aggregateRootId()) : true);
        Validate.validState(encryptedEventSecret.isPresent() ? encryptedEventSecret.get().aggregateRootType().equals(defaultEventId.aggregateRootType()) : true);
        return aggregateRootEventPayloadDeSerializer.deserialize(encryptedEventSecret, eventPayload);
    }

    @Override
    public EventMetadata eventMetaData(final Optional<EncryptedEventSecret> encryptedEventSecret,
                                       final EventMetadataDeserializer eventMetadataDeserializer) {
        Validate.validState(encryptedEventSecret.isPresent() ? encryptedEventSecret.get().aggregateRootId().equals(defaultEventId.aggregateRootId()) : true);
        Validate.validState(encryptedEventSecret.isPresent() ? encryptedEventSecret.get().aggregateRootType().equals(defaultEventId.aggregateRootType()) : true);
        return eventMetadataDeserializer.deserialize(encryptedEventSecret, eventMetaData);
    }

    public Event toEvent(final Optional<EncryptedEventSecret> encryptedEventSecret,
                         final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer,
                         final EventMetadataDeserializer eventMetadataDeserializer) {
        Validate.notNull(encryptedEventSecret);
        Validate.notNull(aggregateRootEventPayloadDeSerializer);
        Validate.notNull(eventMetadataDeserializer);
        Validate.validState(encryptedEventSecret.isPresent() ? encryptedEventSecret.get().aggregateRootId().equals(defaultEventId.aggregateRootId()) : true);
        Validate.validState(encryptedEventSecret.isPresent() ? encryptedEventSecret.get().aggregateRootType().equals(defaultEventId.aggregateRootType()) : true);
        return new Event(this, encryptedEventSecret, aggregateRootEventPayloadDeSerializer, eventMetadataDeserializer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostgreSQLDecryptableEvent that = (PostgreSQLDecryptableEvent) o;
        return Objects.equals(defaultEventId, that.defaultEventId) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(eventType, that.eventType) &&
                Objects.equals(eventMetaData, that.eventMetaData) &&
                Objects.equals(eventPayload, that.eventPayload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(defaultEventId, creationDate, eventType, eventMetaData, eventPayload);
    }

    @Override
    public String toString() {
        return "PostgreSQLDecryptableEvent{" +
                "jdbcEventId=" + defaultEventId +
                ", creationDate=" + creationDate +
                ", eventType='" + eventType + '\'' +
                ", eventMetaData='" + eventMetaData + '\'' +
                ", eventPayload='" + eventPayload + '\'' +
                '}';
    }
}
