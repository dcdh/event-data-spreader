package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.KafkaSource;

public class UnableToDecodeDebeziumEventMessageException extends Exception {

    private final Integer partition;
    private final String topic;
    private final Long offset;

    public UnableToDecodeDebeziumEventMessageException(final KafkaSource kafkaSource,
                                                       final String message) {
        super(message);
        this.partition = kafkaSource.partition();
        this.topic = kafkaSource.topic();
        this.offset = kafkaSource.offset();
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
