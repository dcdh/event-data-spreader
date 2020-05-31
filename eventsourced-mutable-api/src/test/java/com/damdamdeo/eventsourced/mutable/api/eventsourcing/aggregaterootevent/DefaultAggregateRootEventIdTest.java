package com.damdamdeo.eventsourced.mutable.api.eventsourcing.aggregaterootevent;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.DefaultAggregateRootEventId;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class DefaultAggregateRootEventIdTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(DefaultAggregateRootEventId.class).verify();
    }

}
