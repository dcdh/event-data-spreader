package com.damdamdeo.eventdataspreader.eventsourcing.infrastructure;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class VaultEncryptedEventSecretTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(VaultEncryptedEventSecret.class).verify();
    }

}
