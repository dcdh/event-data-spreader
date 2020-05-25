package com.damdamdeo.eventdataspreader.queryside.consumer.payload;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class AccountAggregateRootAccountDebitedAggregateRootEventPayloadConsumerTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(AccountAggregateRootAccountDebitedAggregateRootEventPayloadConsumer.class).verify();
    }

}
