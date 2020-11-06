package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.Operation;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in.DebeziumJsonbEventInKeyRecord;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in.DebeziumJsonbEventInValueRecord;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;

import java.util.stream.Stream;

public enum DebeziumOperation {

    CREATE_OPERATION {

        @Override
        public String debeziumOperation() {
            return "c";
        }

        @Override
        public Operation operation() {
            return Operation.CREATE;
        }

    },

    READ_DUE_TO_SNAPSHOTTING_AT_CONNECTOR_START {

        @Override
        public String debeziumOperation() {
            return "r";
        }

        @Override
        public Operation operation() {
            return Operation.READ;
        }

    };

    public abstract String debeziumOperation();

    public abstract Operation operation();

    public static DebeziumOperation fromDebeziumOperation(final IncomingKafkaRecord<DebeziumJsonbEventInKeyRecord, DebeziumJsonbEventInValueRecord> record) throws UnsupportedDebeziumOperationException {
        final String debeziumOperation = record.getPayload().operation();
        return Stream.of(DebeziumOperation.values())
                .filter(operation -> debeziumOperation.equals(operation.debeziumOperation()))
                .findFirst()
                .orElseThrow(() -> new UnsupportedDebeziumOperationException(record));
    }
}
