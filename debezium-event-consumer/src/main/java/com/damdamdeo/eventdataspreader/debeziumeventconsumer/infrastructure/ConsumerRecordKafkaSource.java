package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

public class ConsumerRecordKafkaSource implements KafkaSource {

    private final Integer partition;
    private final String topic;
    private final Long offset;

    public ConsumerRecordKafkaSource(final IncomingKafkaRecord<JsonObject, JsonObject> record) {
        this.partition = Objects.requireNonNull(record.getPartition());
        this.topic = Objects.requireNonNull(record.getTopic());
        this.offset = Objects.requireNonNull(record.getOffset());
    }

    @Override
    public Integer partition() {
        return partition;
    }

    @Override
    public String topic() {
        return topic;
    }

    @Override
    public Long offset() {
        return offset;
    }

}