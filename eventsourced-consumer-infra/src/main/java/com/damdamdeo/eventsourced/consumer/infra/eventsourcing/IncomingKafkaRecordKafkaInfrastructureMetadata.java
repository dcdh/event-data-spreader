package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in.DebeziumJsonbEventInKeyRecord;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in.DebeziumJsonbEventInValueRecord;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;

import java.util.Objects;

public final class IncomingKafkaRecordKafkaInfrastructureMetadata implements KafkaInfrastructureMetadata {

    private final Integer partition;
    private final String topic;
    private final Long offset;

    public IncomingKafkaRecordKafkaInfrastructureMetadata(final IncomingKafkaRecord<DebeziumJsonbEventInKeyRecord, DebeziumJsonbEventInValueRecord> record) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IncomingKafkaRecordKafkaInfrastructureMetadata that = (IncomingKafkaRecordKafkaInfrastructureMetadata) o;
        return Objects.equals(partition, that.partition) &&
                Objects.equals(topic, that.topic) &&
                Objects.equals(offset, that.offset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partition, topic, offset);
    }

    @Override
    public String toString() {
        return "IncomingKafkaRecordKafkaInfrastructureMetadata{" +
                "partition=" + partition +
                ", topic='" + topic + '\'' +
                ", offset=" + offset +
                '}';
    }

}