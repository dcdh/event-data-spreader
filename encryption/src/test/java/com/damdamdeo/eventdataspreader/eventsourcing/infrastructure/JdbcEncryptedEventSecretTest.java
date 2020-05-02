package com.damdamdeo.eventdataspreader.eventsourcing.infrastructure;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class JdbcEncryptedEventSecretTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(JdbcEncryptedEventSecret.class).verify();
    }

}
