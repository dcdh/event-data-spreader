package com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class UnsupportedAggregateRootEventPayloadTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(UnsupportedAggregateRootEventPayload.class).verify();
    }

}
