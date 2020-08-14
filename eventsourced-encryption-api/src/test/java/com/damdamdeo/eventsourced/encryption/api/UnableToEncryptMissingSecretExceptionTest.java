package com.damdamdeo.eventsourced.encryption.api;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class UnableToEncryptMissingSecretExceptionTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(UnableToEncryptMissingSecretException.class)
                .withOnlyTheseFields("aggregateRootId")
                .verify();
    }

}
