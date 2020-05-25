package com.damdamdeo.eventdataspreader.writeside.consumer.payload;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class GiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumerTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(GiftAggregateRootGiftBoughtAggregateRootEventPayloadConsumer.class).verify();
    }

}
