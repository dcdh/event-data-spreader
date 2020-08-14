package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.*;
import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import io.vertx.core.json.JsonObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public final class DebeziumAggregateRootEventConsumable implements AggregateRootEventConsumable<JsonNode> {

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
    private final JsonNode eventMetaData;
    private final JsonNode eventPayload;
    private final JsonNode materializedState;

    public DebeziumAggregateRootEventConsumable(final DebeziumAggregateRootEventId aggregateRootEventId,
                                                final LocalDateTime creationDate,
                                                final String eventType,
                                                final JsonNode eventMetaData,
                                                final JsonNode eventPayload,
                                                final JsonNode materializedState) {
        this.aggregateRootEventId = Objects.requireNonNull(aggregateRootEventId);
        this.creationDate = Objects.requireNonNull(creationDate);
        this.eventType = Objects.requireNonNull(eventType);
        this.eventMetaData = Objects.requireNonNull(eventMetaData);
        this.eventPayload = Objects.requireNonNull(eventPayload);
        this.materializedState = Objects.requireNonNull(materializedState);
    }

    public DebeziumAggregateRootEventConsumable(final IncomingKafkaRecord<JsonObject, JsonObject> record, final ObjectMapper objectMapper)
            throws UnableToDecodeDebeziumEventMessageException, UnsupportedDebeziumOperationException, JsonProcessingException {
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
        this.eventMetaData = objectMapper.readTree(Objects.requireNonNull(after.getString(EVENT_EVENT_METADATA)));
        this.eventPayload = objectMapper.readTree(Objects.requireNonNull(after.getString(EVENT_EVENT_PAYLOAD)));
        this.materializedState = objectMapper.readTree(Objects.requireNonNull(after.getString(MATERIALIZED_STATE)));
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
    public JsonNode eventMetaData() {
        return eventMetaData;
    }

    @Override
    public JsonNode eventPayload() {
        return eventPayload;
    }

    @Override
    public JsonNode materializedState() {
        return materializedState;
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
