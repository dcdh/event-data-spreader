package com.damdamdeo.eventdataspreader.event.infrastructure.consumer;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class DebeziumIncomingKafkaRecordDecryptableAggregateRootEventTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(DebeziumIncomingKafkaRecordDecryptableAggregateRootEvent.class).verify();
    }

}
