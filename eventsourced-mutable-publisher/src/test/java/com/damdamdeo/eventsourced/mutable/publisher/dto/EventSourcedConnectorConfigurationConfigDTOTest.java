package com.damdamdeo.eventsourced.mutable.publisher.dto;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class EventSourcedConnectorConfigurationConfigDTOTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(EventSourcedConnectorConfigurationConfigDTO.class).verify();
    }

}
