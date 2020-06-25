package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import java.util.Objects;

public class UnableToDecodeDebeziumEventMessageException extends Exception {

    private final Integer partition;
    private final String topic;
    private final Long offset;

    public UnableToDecodeDebeziumEventMessageException(final KafkaInfrastructureMetadata kafkaSource,
                                                       final String message) {
        super(message);
        this.partition = Objects.requireNonNull(kafkaSource.partition());
        this.topic = Objects.requireNonNull(kafkaSource.topic());
        this.offset = Objects.requireNonNull(kafkaSource.offset());
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
}