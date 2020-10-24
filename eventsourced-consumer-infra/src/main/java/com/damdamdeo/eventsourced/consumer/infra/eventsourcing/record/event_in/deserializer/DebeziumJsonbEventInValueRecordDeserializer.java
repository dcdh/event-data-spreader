package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in.deserializer;

import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in.DebeziumJsonbEventInValueRecord;
import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public final class DebeziumJsonbEventInValueRecordDeserializer extends JsonbDeserializer<DebeziumJsonbEventInValueRecord> {

    public DebeziumJsonbEventInValueRecordDeserializer() {
        super(DebeziumJsonbEventInValueRecord.class);
    }

}
