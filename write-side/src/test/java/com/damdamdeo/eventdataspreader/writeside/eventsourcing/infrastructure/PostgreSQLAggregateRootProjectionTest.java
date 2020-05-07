package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class PostgreSQLAggregateRootProjectionTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(PostgreSQLAggregateRootProjection.class).verify();
    }

}
