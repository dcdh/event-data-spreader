package com.damdamdeo.eventsourced.encryption.infra;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class JdbcAggregateRootSecretTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(JdbcAggregateRootSecret.class).verify();
    }

}
