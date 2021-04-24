package com.damdamdeo.eventsourced.mutable.eventsourcing;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class DefaultAggregateRootMaterializedStateTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(DefaultAggregateRootMaterializedState.class).verify();
    }

}
