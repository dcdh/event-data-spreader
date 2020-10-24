package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class IncomingKafkaRecordKafkaInfrastructureMetadataTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(IncomingKafkaRecordKafkaInfrastructureMetadata.class).verify();
    }

}
