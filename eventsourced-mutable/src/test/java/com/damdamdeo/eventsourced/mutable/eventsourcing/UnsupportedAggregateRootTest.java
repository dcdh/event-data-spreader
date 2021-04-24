package com.damdamdeo.eventsourced.mutable.eventsourcing;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class UnsupportedAggregateRootTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(UnsupportedAggregateRoot.class)
                .withOnlyTheseFields("aggregateRootType")
                .verify();
    }

}
