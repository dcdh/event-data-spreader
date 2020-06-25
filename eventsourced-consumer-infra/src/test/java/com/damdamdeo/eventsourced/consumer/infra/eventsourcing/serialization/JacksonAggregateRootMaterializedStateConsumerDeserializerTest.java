package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootMaterializedStateConsumer;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.spi.JacksonAggregateRootMaterializedStateConsumerMixInSubtypeDiscovery;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.spi.JacksonMixInSubtype;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@QuarkusTest
public class JacksonAggregateRootMaterializedStateConsumerDeserializerTest {

    @Inject
    JacksonAggregateRootMaterializedStateConsumerDeserializer jacksonAggregateRootMaterializedStateConsumerDeserializer;

    @Produces
    public JacksonAggregateRootMaterializedStateConsumerMixInSubtypeDiscovery jacksonAggregateRootMaterializedStateConsumerMixInSubtypeDiscovery() {
        return () -> Collections.singletonList(
                new JacksonMixInSubtype<>(TestAggregateRootMaterializedStateConsumer.class, JacksonTestAggregateRootMaterializedStateConsumer.class, "TestAggregateRoot"));
    }

    public static final class TestAggregateRootMaterializedStateConsumer extends AggregateRootMaterializedStateConsumer {

        private final String dummy;

        public TestAggregateRootMaterializedStateConsumer(final String aggregateRootId,
                                                          final String aggregateRootType,
                                                          final Long version,
                                                          final String dummy) {
            super(aggregateRootId, aggregateRootType, version);
            this.dummy = Objects.requireNonNull(dummy);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestAggregateRootMaterializedStateConsumer that = (TestAggregateRootMaterializedStateConsumer) o;
            return Objects.equals(dummy, that.dummy);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dummy);
        }
    }

    public static abstract class JacksonTestAggregateRootMaterializedStateConsumer extends JacksonAggregateRootMaterializedStateConsumer {

        @JsonCreator
        public JacksonTestAggregateRootMaterializedStateConsumer(@JsonProperty("aggregateRootId") final String aggregateRootId,
                                                                 @JsonProperty("aggregateRootType") final String aggregateRootType,
                                                                 @JsonProperty("version") final Long version,
                                                                 @JsonProperty("dummy") final String dummy) {
        }

    }

    @Test
    public void should_deserialize() {
        // Given

        // When
        final AggregateRootMaterializedStateConsumer deserialized = jacksonAggregateRootMaterializedStateConsumerDeserializer.deserialize(mock(Secret.class),
                "{\"@type\": \"TestAggregateRoot\", \"aggregateRootId\": \"aggregateRootId\", \"version\":0, \"aggregateRootType\": \"TestAggregateRoot\", \"dummy\": \"dummy\"}");

        // Then
        assertEquals(new TestAggregateRootMaterializedStateConsumer("aggregateRootId", "TestAggregateRoot", 0l, "dummy"), deserialized);
    }
}
