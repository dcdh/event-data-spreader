package com.damdamdeo.eventsourced.encryption.api;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class UnableToDecryptMissingSecretExceptionTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(UnableToDecryptMissingSecretException.class)
                .withOnlyTheseFields("aggregateRootId")
                .verify();
    }

}
