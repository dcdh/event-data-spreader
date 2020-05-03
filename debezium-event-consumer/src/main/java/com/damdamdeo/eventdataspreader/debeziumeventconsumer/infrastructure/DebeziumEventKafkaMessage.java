package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.*;
import com.damdamdeo.eventdataspreader.event.api.*;
import com.damdamdeo.eventdataspreader.eventsourcing.api.*;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import io.vertx.core.json.JsonObject;

import java.util.*;

public final class DebeziumEventKafkaMessage implements DecryptableEvent {

    private static final String AFTER = "after";
    private static final String OPERATION = "op";;

    private static final String EVENT_CREATION_DATE = "creationdate";

    private static final String EVENT_EVENT_TYPE = "eventtype";
    private static final String EVENT_EVENT_METADATA = "eventmetadata";
    private static final String EVENT_EVENT_PAYLOAD = "eventpayload";

    private final EventId eventId;
    private final Date creationDate;

    private final String eventType;
    private final String eventMetaData;
    private final String eventPayload;

    public DebeziumEventKafkaMessage(final IncomingKafkaRecord<JsonObject, JsonObject> record)
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
        final JsonObject after = record.getPayload().getJsonObject(AFTER);
        this.eventId = new DebeziumEventId(after);
        this.creationDate = new Date(after.getLong(EVENT_CREATION_DATE) / 1000);

        this.eventType = after.getString(EVENT_EVENT_TYPE);
        this.eventMetaData = after.getString(EVENT_EVENT_METADATA);
        this.eventPayload = after.getString(EVENT_EVENT_PAYLOAD);
    }

    @Override
    public EventId eventId() {
        return eventId;
    }

    @Override
    public Date creationDate() {
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
                                       final EventMetadataDeserializer eventMetadataDeserializer) {
        return eventMetadataDeserializer.deserialize(encryptedEventSecret, eventMetaData);
    }

    @Override
    public EventPayload eventPayload(final Optional<EncryptedEventSecret> encryptedEventSecret,
                                     final EventPayloadDeserializer eventPayloadDeserializer) {
        return eventPayloadDeserializer.deserialize(encryptedEventSecret, eventPayload);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DebeziumEventKafkaMessage)) return false;
        DebeziumEventKafkaMessage that = (DebeziumEventKafkaMessage) o;
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
