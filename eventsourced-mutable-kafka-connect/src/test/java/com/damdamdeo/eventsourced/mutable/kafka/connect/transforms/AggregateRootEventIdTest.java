package com.damdamdeo.eventsourced.mutable.kafka.connect.transforms;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class AggregateRootEventIdTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(AggregateRootEventId.class).verify();
    }

}
