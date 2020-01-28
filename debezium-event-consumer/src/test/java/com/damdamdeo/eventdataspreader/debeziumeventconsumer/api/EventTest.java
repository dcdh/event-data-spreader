package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class EventTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(Event.class).verify();
    }

}
