package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class AggregateRootEventTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(AggregateRootEvent.class).verify();
    }

}
