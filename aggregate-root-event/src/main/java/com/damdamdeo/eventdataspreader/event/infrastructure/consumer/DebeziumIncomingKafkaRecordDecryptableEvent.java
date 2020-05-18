package com.damdamdeo.eventdataspreader.event.infrastructure.consumer;

import com.damdamdeo.eventdataspreader.event.api.*;
import com.damdamdeo.eventdataspreader.eventsourcing.api.*;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import io.vertx.core.json.JsonObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public final class DebeziumIncomingKafkaRecordDecryptableEvent implements DecryptableEvent {

    private static final String AFTER = "after";
    private static final String OPERATION = "op";;

    private static final String EVENT_CREATION_DATE = "creationdate";

    private static final String EVENT_EVENT_TYPE = "eventtype";
    private static final String EVENT_EVENT_METADATA = "eventmetadata";
    private static final String EVENT_EVENT_PAYLOAD = "eventpayload";

    private final EventId eventId;
    private final LocalDateTime creationDate;

    private final String eventType;
    private final String eventMetaData;
    private final String eventPayload;

    public DebeziumIncomingKafkaRecordDecryptableEvent(final IncomingKafkaRecord<JsonObject, JsonObject> record)
            throws UnableToDecodeDebeziumEventMessageException {
        if (record.getKey() == null) {
            throw new UnableToDecodeDebeziumEventMessageException(new ConsumerRecordKafkaSource(record),
                    "'Message Key' is missing");
        }
        if (record.getPayload() == null) {
            throw new UnableToDecodeDebeziumEventMessageException(new ConsumerRecordKafkaSource(record),
                    "'Message Payload' is missing");
        }
        if (record.getPayload().getJsonObject(AFTER) == null) {
            throw new UnableToDecodeDebeziumEventMessageException(new ConsumerRecordKafkaSource(record),
                    "'after' is missing");
        }
        if (record.getPayload().getString(OPERATION) == null) {
            throw new UnableToDecodeDebeziumEventMessageException(new ConsumerRecordKafkaSource(record),
                    "'op' is missing");
        }
        final JsonObject after = Objects.requireNonNull(record.getPayload().getJsonObject(AFTER));
        this.eventId = new DebeziumEventId(after);
        final Instant instant = Instant.ofEpochMilli(Objects.requireNonNull(after.getLong(EVENT_CREATION_DATE)) / 1000);
        this.creationDate = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        this.eventType = Objects.requireNonNull(after.getString(EVENT_EVENT_TYPE));
        this.eventMetaData = Objects.requireNonNull(after.getString(EVENT_EVENT_METADATA));
        this.eventPayload = Objects.requireNonNull(after.getString(EVENT_EVENT_PAYLOAD));
    }

    @Override
    public EventId eventId() {
        return eventId;
    }

    @Override
    public LocalDateTime creationDate() {
        return creationDate;
    }

    @Override
    public String eventType() {
        return eventType;
    }

    public String aggregateRootId() {
        return eventId.aggregateRootId();
    }

    public String aggregateRootType() {
        return eventId.aggregateRootType();
    }

    @Override
    public EventMetadata eventMetaData(final Optional<EncryptedEventSecret> encryptedEventSecret,
                                       final EventMetadataDeSerializer eventMetadataDeSerializer) {
        return eventMetadataDeSerializer.deserialize(encryptedEventSecret, eventMetaData);
    }

    @Override
    public EventPayload eventPayload(final Optional<EncryptedEventSecret> encryptedEventSecret,
                                     final EventPayloadDeserializer eventPayloadDeserializer) {
        return eventPayloadDeserializer.deserialize(encryptedEventSecret, eventPayload);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DebeziumIncomingKafkaRecordDecryptableEvent)) return false;
        DebeziumIncomingKafkaRecordDecryptableEvent that = (DebeziumIncomingKafkaRecordDecryptableEvent) o;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(eventType, that.eventType) &&
                Objects.equals(eventMetaData, that.eventMetaData) &&
                Objects.equals(eventPayload, that.eventPayload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, creationDate, eventType, eventMetaData, eventPayload);
    }

    @Override
    public String toString() {
        return "DebeziumEventKafkaMessage{" +
                "eventId=" + eventId +
                ", creationDate=" + creationDate +
                ", eventType='" + eventType + '\'' +
                ", eventMetaData='" + eventMetaData + '\'' +
                ", eventPayload='" + eventPayload + '\'' +
                '}';
    }
}
