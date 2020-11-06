package com.damdamdeo.eventsourced.mutable.publisher.dto;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class DebeziumConnectorConfigurationDTOTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(DebeziumConnectorConfigurationDTO.class).verify();
    }

}
