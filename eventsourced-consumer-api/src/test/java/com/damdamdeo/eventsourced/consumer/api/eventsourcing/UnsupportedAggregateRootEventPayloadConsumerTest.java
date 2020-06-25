package com.damdamdeo.eventsourced.consumer.api.eventsourcing;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class UnsupportedAggregateRootEventPayloadConsumerTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(UnsupportedAggregateRootEventPayloadConsumer.class).verify();
    }

}
