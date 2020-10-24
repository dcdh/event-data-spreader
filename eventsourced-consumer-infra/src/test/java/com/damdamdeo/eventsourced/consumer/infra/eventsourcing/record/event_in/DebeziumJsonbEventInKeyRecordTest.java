package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DebeziumJsonbEventInKeyRecordTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(DebeziumJsonbEventInKeyRecord.class).verify();
    }

    @Test
    public void should_deserialize() throws Exception {
        // Given
        final Jsonb jsonb = JsonbBuilder.create();
        final String jsonContent = new String(Files.readAllBytes(Paths.get(getClass().getResource("/eventsourcing/DebeziumEventInKeyRecord.json").toURI())));

        // When
        final DebeziumJsonbEventInKeyRecord debeziumJsonBEventInKeyRecord = jsonb.fromJson(jsonContent, DebeziumJsonbEventInKeyRecord.class);

        // Then
        assertEquals(new DebeziumJsonbEventInKeyRecord("AccountAggregateRoot", "damdamdeo", 0l),
                debeziumJsonBEventInKeyRecord);
    }

}
