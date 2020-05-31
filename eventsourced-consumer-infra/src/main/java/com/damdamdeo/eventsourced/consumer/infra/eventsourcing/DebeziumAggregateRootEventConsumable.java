package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.*;
import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import io.vertx.core.json.JsonObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public final class DebeziumAggregateRootEventConsumable {

    private static final String AFTER = "after";
    private static final String OPERATION = "op";;

    private static final String EVENT_CREATION_DATE = "creationdate";

    private static final String EVENT_EVENT_TYPE = "eventtype";
    private static final String EVENT_EVENT_METADATA = "eventmetadata";
    private static final String EVENT_EVENT_PAYLOAD = "eventpayload";

    private final DebeziumAggregateRootEventId aggregateRootEventId;
    private final LocalDateTime creationDate;

    private final String eventType;
    private final String eventMetaData;
    private final String eventPayload;

    public DebeziumAggregateRootEventConsumable(final DebeziumAggregateRootEventId aggregateRootEventId,
                                                final LocalDateTime creationDate,
                                                final String eventType,
                                                final String eventMetaData,
                                                final String eventPayload) {
        this.aggregateRootEventId = Objects.requireNonNull(aggregateRootEventId);
        this.creationDate = Objects.requireNonNull(creationDate);
        this.eventType = Objects.requireNonNull(eventType);
        this.eventMetaData = Objects.requireNonNull(eventMetaData);
        this.eventPayload = Objects.requireNonNull(eventPayload);
    }

    public DebeziumAggregateRootEventConsumable(final IncomingKafkaRecord<JsonObject, JsonObject> record)
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

    public AggregateRootEventId eventId() {
        return aggregateRootEventId;
    }

    public LocalDateTime creationDate() {
        return creationDate;
    }

    public String eventType() {
        return eventType;
    }

    public AggregateRootEventMetadataConsumer eventMetaData(final Optional<AggregateRootSecret> aggregateRootSecret,
                                                            final AggregateRootEventMetadataConsumerDeserializer aggregateRootEventMetadataConsumerDeSerializer) {
        return aggregateRootEventMetadataConsumerDeSerializer.deserialize(aggregateRootSecret, eventMetaData);
    }

    public AggregateRootEventPayloadConsumer eventPayload(final Optional<AggregateRootSecret> aggregateRootSecret,
                                                          final AggregateRootEventPayloadConsumerDeserializer aggregateRootEventPayloadConsumerDeserializer) {
        return aggregateRootEventPayloadConsumerDeserializer.deserialize(aggregateRootSecret, eventPayload);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DebeziumAggregateRootEventConsumable)) return false;
        DebeziumAggregateRootEventConsumable that = (DebeziumAggregateRootEventConsumable) o;
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
