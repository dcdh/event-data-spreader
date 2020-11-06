package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in.deserializer;

import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in.DebeziumJsonbEventInPayloadRecord;
import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public final class DebeziumJsonbEventInValueRecordDeserializer extends JsonbDeserializer<DebeziumJsonbEventInPayloadRecord> {

    public DebeziumJsonbEventInValueRecordDeserializer() {
        super(DebeziumJsonbEventInPayloadRecord.class);
    }

}
