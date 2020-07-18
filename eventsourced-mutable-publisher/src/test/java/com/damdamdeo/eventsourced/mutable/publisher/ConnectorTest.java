package com.damdamdeo.eventsourced.mutable.publisher;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class ConnectorTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(Connector.class).verify();
    }

}
