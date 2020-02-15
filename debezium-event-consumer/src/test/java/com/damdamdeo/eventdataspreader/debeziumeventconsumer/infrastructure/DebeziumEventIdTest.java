package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class DebeziumEventIdTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(DebeziumEventId.class).verify();
    }

}
