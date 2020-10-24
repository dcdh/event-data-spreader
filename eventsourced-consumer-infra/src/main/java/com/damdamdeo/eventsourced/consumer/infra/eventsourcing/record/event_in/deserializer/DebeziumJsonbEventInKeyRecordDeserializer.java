package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in.deserializer;

import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in.DebeziumJsonbEventInKeyRecord;
import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public final class DebeziumJsonbEventInKeyRecordDeserializer extends JsonbDeserializer<DebeziumJsonbEventInKeyRecord> {

    public DebeziumJsonbEventInKeyRecordDeserializer() {
        super(DebeziumJsonbEventInKeyRecord.class);
    }

}
