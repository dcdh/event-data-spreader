package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class AggregateRootIdTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(AggregateRootId.class).verify();
    }

}
