package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class PostgreSQLAggregateRootEventIdTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(PostgreSQLAggregateRootEventId.class).verify();
    }

}
