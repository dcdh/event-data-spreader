package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class DebeziumJsonbAggregateRootIdTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(DebeziumJsonbAggregateRootId.class).verify();
    }

}
