package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.KafkaSource;
import io.smallrye.reactive.messaging.kafka.ReceivedKafkaMessage;
import io.vertx.core.json.JsonObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class ConsumerRecordKafkaSource implements KafkaSource {

    private final Integer partition;
    private final String topic;
    private final Long offset;

    public ConsumerRecordKafkaSource(final ReceivedKafkaMessage<JsonObject, JsonObject> message) {
        this(message.unwrap());
    }

    public ConsumerRecordKafkaSource(final ConsumerRecord<JsonObject, JsonObject> consumerRecord) {
        this.partition = consumerRecord.partition();
        this.topic = consumerRecord.topic();
        this.offset = consumerRecord.offset();
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