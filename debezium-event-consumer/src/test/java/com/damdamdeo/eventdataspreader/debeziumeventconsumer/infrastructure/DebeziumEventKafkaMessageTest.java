package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class DebeziumEventKafkaMessageTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(DebeziumEventKafkaMessage.class).verify();
    }

}
