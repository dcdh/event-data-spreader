package com.damdamdeo.eventdataspreader.queryside.consumer.metadata;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class UserAggregateRootEventMetadataConsumerTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(UserAggregateRootEventMetadataConsumer.class).verify();
    }

}
