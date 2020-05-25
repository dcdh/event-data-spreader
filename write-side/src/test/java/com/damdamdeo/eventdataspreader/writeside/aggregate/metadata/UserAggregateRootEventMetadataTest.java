package com.damdamdeo.eventdataspreader.writeside.aggregate.metadata;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class UserAggregateRootEventMetadataTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(UserAggregateRootEventMetadata.class).verify();
    }

}
