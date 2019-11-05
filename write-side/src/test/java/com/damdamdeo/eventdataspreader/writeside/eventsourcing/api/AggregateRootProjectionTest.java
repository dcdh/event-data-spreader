package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class AggregateRootProjectionTest {

    @Test
    public void should_be_equals() {
        EqualsVerifier.forClass(AggregateRootProjection.class).verify();
    }

}
