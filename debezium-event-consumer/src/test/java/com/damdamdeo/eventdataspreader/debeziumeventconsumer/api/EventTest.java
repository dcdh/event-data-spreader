package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

import com.damdamdeo.eventdataspreader.event.api.Event;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class EventTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(Event.class).verify();
    }

}
