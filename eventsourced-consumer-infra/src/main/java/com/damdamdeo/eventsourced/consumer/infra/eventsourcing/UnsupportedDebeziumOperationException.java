package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

public final class UnsupportedDebeziumOperationException extends Exception {

    private final Integer partition;
    private final String topic;
    private final Long offset;
    private final String key;
    private final String payload;

    public UnsupportedDebeziumOperationException(final IncomingKafkaRecord<JsonObject, JsonObject> record) {
        this.partition = Objects.requireNonNull(record.getPartition());
        this.topic = Objects.requireNonNull(record.getTopic());
        this.offset = Objects.requireNonNull(record.getOffset());
        this.key = Objects.requireNonNull(record.getKey().encodePrettily());
        this.payload = Objects.requireNonNull(record.getPayload().encodePrettily());
    }

    public Integer partition() {
        return partition;
    }

    public String topic() {
        return topic;
    }

    public Long offset() {
        return offset;
    }

    public String key() {
        return key;
    }

    public String payload() {
        return payload;
    }
}
