package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in.DebeziumJsonbEventInValueRecord;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in.DebeziumJsonbEventInKeyRecord;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;

import java.util.Objects;

public final class UnsupportedDebeziumOperationException extends Exception {

    private final Integer partition;
    private final String topic;
    private final Long offset;
    private final DebeziumJsonbEventInKeyRecord key;
    private final DebeziumJsonbEventInValueRecord payload;

    public UnsupportedDebeziumOperationException(final IncomingKafkaRecord<DebeziumJsonbEventInKeyRecord, DebeziumJsonbEventInValueRecord> record) {
        this.partition = Objects.requireNonNull(record.getPartition());
        this.topic = Objects.requireNonNull(record.getTopic());
        this.offset = Objects.requireNonNull(record.getOffset());
        this.key = Objects.requireNonNull(record.getKey());
        this.payload = Objects.requireNonNull(record.getPayload());
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

    public DebeziumJsonbEventInKeyRecord key() {
        return key;
    }

    public DebeziumJsonbEventInValueRecord payload() {
        return payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnsupportedDebeziumOperationException)) return false;
        UnsupportedDebeziumOperationException that = (UnsupportedDebeziumOperationException) o;
        return Objects.equals(partition, that.partition) &&
                Objects.equals(topic, that.topic) &&
                Objects.equals(offset, that.offset) &&
                Objects.equals(key, that.key) &&
                Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partition, topic, offset, key, payload);
    }

    @Override
    public String toString() {
        return "UnsupportedDebeziumOperationException{" +
                "partition=" + partition +
                ", topic='" + topic + '\'' +
                ", offset=" + offset +
                ", key=" + key +
                ", payload=" + payload +
                "} " + super.toString();
    }
}
