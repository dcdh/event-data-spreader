package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.Event;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

public final class DebeziumEventKafkaMessage implements Event {

    private static final String PAYLOAD = "payload";
    private static final String AFTER = "after";
    private static final String OPERATION = "op";
    private static final String CREATE_OPERATION = "c";
    private static final String READ_OPERATION = "r";

    private static final String EVENT_EVENT_ID = "eventid";
    private static final String EVENT_AGGREGATE_ROOT_ID = "aggregaterootid";
    private static final String EVENT_AGGREGATE_ROOT_TYPE = "aggregateroottype";
    private static final String EVENT_CREATION_DATE = "creationdate";
    private static final String EVENT_EVENT_TYPE = "eventtype";
    private static final String EVENT_METADATA = "metadata";
    private static final String EVENT_EVENT_PAYLOAD = "eventpayload";
    private static final String EVENT_VERSION = "version";

    private final UUID eventId;
    private final String aggregateRootId;
    private final String aggregateRootType;
    private final Date creationDate;
    private final String eventType;
    private final JsonObject metadata;
    private final JsonObject payload;
    private final Long version;

    public DebeziumEventKafkaMessage(final KafkaMessage<JsonObject, JsonObject> message) throws UnableToDecodeDebeziumEventMessageException {
        if (message.getKey() == null) {
            throw new UnableToDecodeDebeziumEventMessageException("'Message Key' is missing");
        }
        if (message.getKey().getJsonObject(PAYLOAD) == null) {
            throw new UnableToDecodeDebeziumEventMessageException("Message Key 'payload' is missing");
        }
        if (message.getKey().getJsonObject(PAYLOAD).getString(EVENT_EVENT_ID) == null) {
            throw new UnableToDecodeDebeziumEventMessageException("Message Key 'payload' is missing");
        }
        if (message.getPayload() == null) {
            throw new UnableToDecodeDebeziumEventMessageException("'Message Payload' is missing");
        }
        if (message.getPayload().getJsonObject(PAYLOAD) == null) {
            throw new UnableToDecodeDebeziumEventMessageException("Message Payload 'payload' is missing");
        }
        if (message.getPayload().getJsonObject(PAYLOAD).getJsonObject(AFTER) == null) {
            throw new UnableToDecodeDebeziumEventMessageException("'after' is missing");
        }
        if (message.getPayload().getJsonObject(PAYLOAD).getString(OPERATION) == null) {
            throw new UnableToDecodeDebeziumEventMessageException("'oc' is missing");
        }
        if (!Arrays.asList(CREATE_OPERATION, READ_OPERATION).contains(message.getPayload().getJsonObject(PAYLOAD).getString(OPERATION))) {
            throw new UnableToDecodeDebeziumEventMessageException("only debezium create or read operation - data inserted in database - are supported");
        }
        if (!message.getKey().getJsonObject(PAYLOAD).getString(EVENT_EVENT_ID).equals(message.getPayload().getJsonObject(PAYLOAD).getJsonObject(AFTER).getString(EVENT_EVENT_ID))) {
            throw new UnableToDecodeDebeziumEventMessageException("events mismatch");
        }
        final JsonObject after = message.getPayload().getJsonObject(PAYLOAD).getJsonObject(AFTER);
        this.eventId = UUID.fromString(after.getString(EVENT_EVENT_ID));
        this.aggregateRootId = after.getString(EVENT_AGGREGATE_ROOT_ID);
        this.aggregateRootType = after.getString(EVENT_AGGREGATE_ROOT_TYPE);
        this.creationDate = new Date(after.getLong(EVENT_CREATION_DATE) / 1000);
        this.eventType = after.getString(EVENT_EVENT_TYPE);
        this.metadata = new JsonObject(after.getString(EVENT_METADATA));
        this.payload = new JsonObject(after.getString(EVENT_EVENT_PAYLOAD));
        this.version = after.getLong(EVENT_VERSION);
    }

    @Override
    public UUID eventId() {
        return eventId;
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
    public String eventType() {
        return eventType;
    }

    @Override
    public JsonObject metadata() {
        return metadata;
    }

    @Override
    public JsonObject payload() {
        return payload;
    }

    @Override
    public Long version() {
        return version;
    }

}
