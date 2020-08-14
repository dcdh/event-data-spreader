package com.damdamdeo.eventsourced.encryption.infra.jackson;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class JacksonAggregateRootIdTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(JacksonAggregateRootId.class).verify();
    }

}
