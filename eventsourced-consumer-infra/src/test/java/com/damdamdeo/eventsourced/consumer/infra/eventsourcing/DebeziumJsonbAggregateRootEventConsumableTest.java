package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class DebeziumJsonbAggregateRootEventConsumableTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(DebeziumJsonbAggregateRootEventConsumable.class).verify();
    }

}
