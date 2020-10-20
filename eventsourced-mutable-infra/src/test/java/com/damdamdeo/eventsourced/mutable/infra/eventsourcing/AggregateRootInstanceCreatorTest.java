package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class AggregateRootInstanceCreatorTest {

    public static final class TestAggregateRoot extends AggregateRoot {

        public TestAggregateRoot(String aggregateRootId) {
            super(aggregateRootId);
        }
    }

    @Inject
    AggregateRootInstanceCreator aggregateRootInstanceCreator;

    @Test
    public void should_create_instance() {
        // Given

        // When
        final AggregateRoot instanceCreated = aggregateRootInstanceCreator.createNewInstance(TestAggregateRoot.class, "aggregateRootId");

        // Then
        assertEquals(new TestAggregateRoot("aggregateRootId"), instanceCreated);
    }

}
