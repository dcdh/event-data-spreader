package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class PostgreSQLDecryptableEventTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(PostgreSQLDecryptableEvent.class).verify();
    }

}
