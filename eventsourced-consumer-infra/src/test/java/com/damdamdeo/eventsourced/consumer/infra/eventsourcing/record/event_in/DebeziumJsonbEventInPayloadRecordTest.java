package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DebeziumJsonbEventInPayloadRecordTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(DebeziumJsonbEventInPayloadRecord.class).verify();
    }

    @Test
    public void should_deserialize() throws Exception {
        // Given
        final Jsonb jsonb = JsonbBuilder.create();
        final String jsonContent = new String(Files.readAllBytes(Paths.get(getClass().getResource("/eventsourcing/DebeziumEventInValueRecord.json").toURI())));

        // When
        final DebeziumJsonbEventInPayloadRecord debeziumEventInKeyRecord = jsonb.fromJson(jsonContent, DebeziumJsonbEventInPayloadRecord.class);

        // Then
        assertEquals(new DebeziumJsonbEventInPayloadRecord(
                        new DebeziumJsonbAggregateRootEvent("AccountAggregateRoot",
                                "damdamdeo",
                                0l,
                                1569174260987000l,
                                "AccountDebited",
                                "{\"executedBy\": \"damdamdeo\"}",
                                "{\"owner\": \"damdamdeo\", \"price\": \"100.00\", \"balance\": \"900.00\"}",
                                "{\"aggregateRootId\": \"damdamdeo\", \"version\":0, \"aggregateRootType\": \"AccountAggregateRoot\", \"balance\": \"900.00\"}"),
                        "c"),
                debeziumEventInKeyRecord);
    }

}
