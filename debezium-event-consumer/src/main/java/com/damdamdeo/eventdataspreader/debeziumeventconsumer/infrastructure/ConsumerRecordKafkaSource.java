package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.KafkaSource;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import io.vertx.core.json.JsonObject;

public class ConsumerRecordKafkaSource implements KafkaSource {

    private final Integer partition;
    private final String topic;
    private final Long offset;

    public ConsumerRecordKafkaSource(final IncomingKafkaRecord<JsonObject, JsonObject> record) {
        this.partition = record.getPartition();
        this.topic = record.getTopic();
        this.offset = record.getOffset();
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