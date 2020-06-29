package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.*;
import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import io.vertx.core.json.JsonObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public final class DebeziumAggregateRootEventConsumable {

    private static final String CREATE_OPERATION = "c";
    private static final String READ_DUE_TO_SNAPSHOTTING_AT_CONNECTOR_START = "r";

    private static final String AFTER = "after";
    private static final String OPERATION = "op";;

    private static final String EVENT_CREATION_DATE = "creationdate";

    private static final String EVENT_EVENT_TYPE = "eventtype";
    private static final String EVENT_EVENT_METADATA = "eventmetadata";
    private static final String EVENT_EVENT_PAYLOAD = "eventpayload";
    private static final String MATERIALIZED_STATE = "materializedstate";

    private final DebeziumAggregateRootEventId aggregateRootEventId;
    private final LocalDateTime creationDate;

    private final String eventType;
    private final String eventMetaData;
    private final String eventPayload;
    private final String materializedState;

    public DebeziumAggregateRootEventConsumable(final DebeziumAggregateRootEventId aggregateRootEventId,
                                                final LocalDateTime creationDate,
                                                final String eventType,
                                                final String eventMetaData,
                                                final String eventPayload,
                                                final String materializedState) {
        this.aggregateRootEventId = Objects.requireNonNull(aggregateRootEventId);
        this.creationDate = Objects.requireNonNull(creationDate);
        this.eventType = Objects.requireNonNull(eventType);
        this.eventMetaData = Objects.requireNonNull(eventMetaData);
        this.eventPayload = Objects.requireNonNull(eventPayload);
        this.materializedState = Objects.requireNonNull(materializedState);
    }

    public DebeziumAggregateRootEventConsumable(final IncomingKafkaRecord<JsonObject, JsonObject> record)
            throws UnableToDecodeDebeziumEventMessageException, UnsupportedDebeziumOperationException {
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
        if (!Arrays.asList(CREATE_OPERATION,
                READ_DUE_TO_SNAPSHOTTING_AT_CONNECTOR_START).contains(record.getPayload().getString(OPERATION))) {
            throw new UnsupportedDebeziumOperationException(record);
        }
        final JsonObject after = Objects.requireNonNull(record.getPayload().getJsonObject(AFTER));
        this.aggregateRootEventId = new DebeziumAggregateRootEventId(after);
        final Instant instant = Instant.ofEpochMilli(Objects.requireNonNull(after.getLong(EVENT_CREATION_DATE)) / 1000);
        this.creationDate = instant.atZone(ZoneOffset.UTC).toLocalDateTime();
        this.eventType = Objects.requireNonNull(after.getString(EVENT_EVENT_TYPE));
        this.eventMetaData = Objects.requireNonNull(after.getString(EVENT_EVENT_METADATA));
        this.eventPayload = Objects.requireNonNull(after.getString(EVENT_EVENT_PAYLOAD));
        this.materializedState = Objects.requireNonNull(after.getString(MATERIALIZED_STATE));
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

    public AggregateRootEventMetadataConsumer eventMetaData(final Secret secret,
                                                            final AggregateRootEventMetadataConsumerDeserializer aggregateRootEventMetadataConsumerDeSerializer) {
        return aggregateRootEventMetadataConsumerDeSerializer.deserialize(secret, eventMetaData);
    }

    public AggregateRootEventPayloadConsumer eventPayload(final Secret secret,
                                                          final AggregateRootEventPayloadConsumerDeserializer aggregateRootEventPayloadConsumerDeserializer) {
        return aggregateRootEventPayloadConsumerDeserializer.deserialize(secret, eventPayload);
    }

    public AggregateRootMaterializedStateConsumer materializedState(final Secret secret,
                                                                    final AggregateRootMaterializedStateConsumerDeserializer aggregateRootMaterializedStateConsumerDeserializer) {
        return aggregateRootMaterializedStateConsumerDeserializer.deserialize(secret, materializedState);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DebeziumAggregateRootEventConsumable that = (DebeziumAggregateRootEventConsumable) o;
        return Objects.equals(aggregateRootEventId, that.aggregateRootEventId) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(eventType, that.eventType) &&
                Objects.equals(eventMetaData, that.eventMetaData) &&
                Objects.equals(eventPayload, that.eventPayload) &&
                Objects.equals(materializedState, that.materializedState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootEventId, creationDate, eventType, eventMetaData, eventPayload, materializedState);
    }

    @Override
    public String toString() {
        return "DebeziumAggregateRootEventConsumable{" +
                "aggregateRootEventId=" + aggregateRootEventId +
                ", creationDate=" + creationDate +
                ", eventType='" + eventType + '\'' +
                ", eventMetaData='" + eventMetaData + '\'' +
                ", eventPayload='" + eventPayload + '\'' +
                ", materializedState='" + materializedState + '\'' +
                '}';
    }
}
