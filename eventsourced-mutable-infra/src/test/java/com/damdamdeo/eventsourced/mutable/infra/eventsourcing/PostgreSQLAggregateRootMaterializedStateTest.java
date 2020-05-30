package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class PostgreSQLAggregateRootMaterializedStateTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(PostgreSQLAggregateRootMaterializedState.class).verify();
    }

}
