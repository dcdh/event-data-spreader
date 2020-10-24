package com.damdamdeo.eventsourced.encryption.infra.jsonb;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class JsonObjectEncryptedAggregateRootIdTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(JsonObjectEncryptedAggregateRootId.class).verify();
    }

}
