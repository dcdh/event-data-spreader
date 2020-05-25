package com.damdamdeo.eventdataspreader.event.infrastructure.consumer;

import com.damdamdeo.eventdataspreader.event.api.*;
import com.damdamdeo.eventdataspreader.event.api.consumer.*;
import com.damdamdeo.eventdataspreader.eventsourcing.api.*;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import io.vertx.core.json.JsonObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public final class DebeziumIncomingKafkaRecordDecryptableAggregateRootEvent implements DecryptableAggregateRootEvent {

    private static final String AFTER = "after";
    private static final String OPERATION = "op";;

    private static final String EVENT_CREATION_DATE = "creationdate";

    private static final String EVENT_EVENT_TYPE = "eventtype";
    private static final String EVENT_EVENT_METADATA = "eventmetadata";
    private static final String EVENT_EVENT_PAYLOAD = "eventpayload";

    private final AggregateRootEventId aggregateRootEventId;
    private final LocalDateTime creationDate;

    private final String eventType;
    private final String eventMetaData;
    private final String eventPayload;

    public DebeziumIncomingKafkaRecordDecryptableAggregateRootEvent(final IncomingKafkaRecord<JsonObject, JsonObject> record)
            throws UnableToDecodeDebeziumEventMessageException {
        if (record.getKey() == null) {
            throw new UnableToDecodeDebeziumEventMessageException(new ConsumerRecordKafkaInfrastructureMetadata(record),
                    "'Message Key' is missing");
        }
        if (record.getPayload() == null) {
            throw new UnableToDecodeDebeziumEventMessageException(new ConsumerRecordKafkaInfrastructureMetadata(record),
                    "'Message Payload' is missing");
        }
        if (record.getPayload().getJsonObject(AFTER) == null) {
            throw new UnableToDecodeDebeziumEventMessageException(new ConsumerRecordKafkaInfrastructureMetadata(record),
                    "'after' is missing");
        }
        if (record.getPayload().getString(OPERATION) == null) {
            throw new UnableToDecodeDebeziumEventMessageException(new ConsumerRecordKafkaInfrastructureMetadata(record),
                    "'op' is missing");
        }
        final JsonObject after = Objects.requireNonNull(record.getPayload().getJsonObject(AFTER));
        this.aggregateRootEventId = new DebeziumAggregateRootEventId(after);
        final Instant instant = Instant.ofEpochMilli(Objects.requireNonNull(after.getLong(EVENT_CREATION_DATE)) / 1000);
        this.creationDate = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        this.eventType = Objects.requireNonNull(after.getString(EVENT_EVENT_TYPE));
        this.eventMetaData = Objects.requireNonNull(after.getString(EVENT_EVENT_METADATA));
        this.eventPayload = Objects.requireNonNull(after.getString(EVENT_EVENT_PAYLOAD));
    }

    @Override
    public AggregateRootEventId eventId() {
        return aggregateRootEventId;
    }

    @Override
    public LocalDateTime creationDate() {
        return creationDate;
    }

    @Override
    public String eventType() {
        return eventType;
    }

    @Override
    public AggregateRootEventMetadataConsumer eventMetaData(final Optional<EncryptedEventSecret> encryptedEventSecret,
                                                            final AggregateRootEventMetadataConsumerDeserializer aggregateRootEventMetadataConsumerDeSerializer) {
        return aggregateRootEventMetadataConsumerDeSerializer.deserialize(encryptedEventSecret, eventMetaData);
    }

    @Override
    public AggregateRootEventPayloadConsumer eventPayload(final Optional<EncryptedEventSecret> encryptedEventSecret,
                                                          final AggregateRootEventPayloadConsumerDeserializer aggregateRootEventPayloadConsumerDeserializer) {
        return aggregateRootEventPayloadConsumerDeserializer.deserialize(encryptedEventSecret, eventPayload);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DebeziumIncomingKafkaRecordDecryptableAggregateRootEvent)) return false;
        DebeziumIncomingKafkaRecordDecryptableAggregateRootEvent that = (DebeziumIncomingKafkaRecordDecryptableAggregateRootEvent) o;
        return Objects.equals(aggregateRootEventId, that.aggregateRootEventId) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(eventType, that.eventType) &&
                Objects.equals(eventMetaData, that.eventMetaData) &&
                Objects.equals(eventPayload, that.eventPayload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootEventId, creationDate, eventType, eventMetaData, eventPayload);
    }

    @Override
    public String toString() {
        return "DebeziumIncomingKafkaRecordDecryptableAggregateRootEvent{" +
                "eventId=" + aggregateRootEventId +
                ", creationDate=" + creationDate +
                ", eventType='" + eventType + '\'' +
                ", eventMetaData='" + eventMetaData + '\'' +
                ", eventPayload='" + eventPayload + '\'' +
                '}';
    }
}
