package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.*;
import com.damdamdeo.eventdataspreader.eventsourcing.api.*;
import io.smallrye.reactive.messaging.kafka.ReceivedKafkaMessage;
import io.vertx.core.json.JsonObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.*;

public final class DebeziumEventKafkaMessage implements DecryptableEvent, EncryptedEventSecret {

    private static final String AFTER = "after";
    private static final String OPERATION = "op";;

    private static final String EVENT_ID = "id";
    private static final String EVENT_AGGREGATE_ROOT_ID = "aggregaterootid";
    private static final String EVENT_AGGREGATE_ROOT_TYPE = "aggregateroottype";
    private static final String EVENT_VERSION = "version";
    private static final String EVENT_CREATION_DATE = "creationdate";
    private static final String EVENT_ENCRYPTED_EVENT_TYPE = "encryptedeventtype";

    private static final String EVENT_SECRET = "secret";

    private static final String EVENT_EVENT_TYPE = "eventtype";
    private static final String EVENT_EVENT_METADATA = "eventmetadata";
    private static final String EVENT_EVENT_PAYLOAD = "eventpayload";

    private final String id;
    private final String aggregateRootId;
    private final String aggregateRootType;
    private final Long version;
    private final Date creationDate;
    private final EncryptedEventType encryptedEventType;

    private final String secret;

    private final String eventType;
    private final String eventMetaData;
    private final String eventPayload;

    public DebeziumEventKafkaMessage(final ReceivedKafkaMessage<JsonObject, JsonObject> message)
            throws UnableToDecodeDebeziumEventMessageException {
        final ConsumerRecord<JsonObject, JsonObject> consumerRecord = message.unwrap();
        if (message.getKey() == null) {
            throw new UnableToDecodeDebeziumEventMessageException(new ConsumerRecordKafkaSource(consumerRecord),
                    "'Message Key' is missing");
        }
        if (message.getKey().getString(EVENT_ID) == null) {
            throw new UnableToDecodeDebeziumEventMessageException(new ConsumerRecordKafkaSource(consumerRecord),
                    "Message Key payload 'id' is missing");
        }
        if (message.getPayload() == null) {
            throw new UnableToDecodeDebeziumEventMessageException(new ConsumerRecordKafkaSource(consumerRecord),
                    "'Message Payload' is missing");
        }
        if (message.getPayload().getJsonObject(AFTER) == null) {
            throw new UnableToDecodeDebeziumEventMessageException(new ConsumerRecordKafkaSource(consumerRecord),
                    "'after' is missing");
        }
        if (message.getPayload().getString(OPERATION) == null) {
            throw new UnableToDecodeDebeziumEventMessageException(new ConsumerRecordKafkaSource(consumerRecord),
                    "'op' is missing");
        }
        if (!message.getKey().getString(EVENT_ID).equals(message.getPayload().getJsonObject(AFTER).getString(EVENT_ID))) {
            throw new UnableToDecodeDebeziumEventMessageException(new ConsumerRecordKafkaSource(consumerRecord),
                    "events mismatch");
        }
        final JsonObject after = message.getPayload().getJsonObject(AFTER);
        this.id = after.getString(EVENT_ID);
        this.aggregateRootId = after.getString(EVENT_AGGREGATE_ROOT_ID);
        this.aggregateRootType = after.getString(EVENT_AGGREGATE_ROOT_TYPE);
        this.version = after.getLong(EVENT_VERSION);
        this.creationDate = new Date(after.getLong(EVENT_CREATION_DATE) / 1000);
        this.encryptedEventType = EncryptedEventType.valueOf(after.getString(EVENT_ENCRYPTED_EVENT_TYPE));

        this.secret = after.getString(EVENT_SECRET);

        this.eventType = after.getString(EVENT_EVENT_TYPE);
        this.eventMetaData = after.getString(EVENT_EVENT_METADATA);
        this.eventPayload = after.getString(EVENT_EVENT_PAYLOAD);
    }

    @Override
    public String eventId() {
        return id;
    }

    @Override
    public String aggregateRootId() {
        return aggregateRootId;
    }

    @Override
    public String aggregateRootType() {
        return aggregateRootType;
    }

    @Override
    public Date creationDate() {
        return creationDate;
    }

    @Override
    public String secret() {
        return secret;
    }

    @Override
    public String eventType() {
        return eventType;
    }

    public EncryptedEventType encryptedEventType() {
        return encryptedEventType;
    }

    @Override
    public EventMetadata eventMetaData(final EncryptedEventSecret encryptedEventSecret,
                                       final EventMetadataDeserializer eventMetadataDeserializer) {
        return eventMetadataDeserializer.deserialize(encryptedEventSecret, eventMetaData);
    }

    @Override
    public EventPayload eventPayload(final EncryptedEventSecret encryptedEventSecret,
                                     final EventPayloadDeserializer eventPayloadDeserializer) {
        return eventPayloadDeserializer.deserialize(encryptedEventSecret, eventPayload);
    }

    @Override
    public Long version() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DebeziumEventKafkaMessage)) return false;
        DebeziumEventKafkaMessage that = (DebeziumEventKafkaMessage) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(aggregateRootId, that.aggregateRootId) &&
                Objects.equals(aggregateRootType, that.aggregateRootType) &&
                Objects.equals(version, that.version) &&
                Objects.equals(creationDate, that.creationDate) &&
                encryptedEventType == that.encryptedEventType &&
                Objects.equals(secret, that.secret) &&
                Objects.equals(eventType, that.eventType) &&
                Objects.equals(eventMetaData, that.eventMetaData) &&
                Objects.equals(eventPayload, that.eventPayload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, aggregateRootId, aggregateRootType, version, creationDate, encryptedEventType, secret, eventType, eventMetaData, eventPayload);
    }

    @Override
    public String toString() {
        return "DebeziumEventKafkaMessage{" +
                "id='" + id + '\'' +
                ", aggregateRootId='" + aggregateRootId + '\'' +
                ", aggregateRootType='" + aggregateRootType + '\'' +
                ", version=" + version +
                ", creationDate=" + creationDate +
                ", encryptedEventType=" + encryptedEventType +
                ", secret='" + secret + '\'' +
                ", eventType='" + eventType + '\'' +
                ", eventMetaData='" + eventMetaData + '\'' +
                ", eventPayload='" + eventPayload + '\'' +
                '}';
    }
}
