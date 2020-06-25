package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventPayloadConsumer;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.spi.JacksonAggregateRootEventPayloadConsumerMixInSubtypeDiscovery;
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
public class JacksonAggregateRootEventPayloadConsumerDeserializerTest {

    @Inject
    JacksonAggregateRootEventPayloadConsumerDeserializer jacksonAggregateRootEventPayloadConsumerDeserializer;

    @Produces
    public JacksonAggregateRootEventPayloadConsumerMixInSubtypeDiscovery jacksonAggregateRootEventPayloadConsumerMixinSubtypeDiscovery() {
        return () -> Collections.singletonList(
                new JacksonMixInSubtype<>(TestAggregateRootEventPayloadConsumer.class, JacksonTestAggregateRootEventPayloadConsumer.class, "TestAggregateRootEventPayload"));
    }

    public static final class TestAggregateRootEventPayloadConsumer extends AggregateRootEventPayloadConsumer {

        private final String dummy;

        public TestAggregateRootEventPayloadConsumer(final String dummy) {
            this.dummy = Objects.requireNonNull(dummy);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestAggregateRootEventPayloadConsumer that = (TestAggregateRootEventPayloadConsumer) o;
            return Objects.equals(dummy, that.dummy);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dummy);
        }
    }

    public static abstract class JacksonTestAggregateRootEventPayloadConsumer extends JacksonAggregateRootEventPayloadConsumer {

        @JsonCreator
        public JacksonTestAggregateRootEventPayloadConsumer(@JsonProperty("dummy") final String dummy) {
        }

    }

    @Test
    public void should_deserialize() {
        // Given

        // When
        final AggregateRootEventPayloadConsumer deserialized = jacksonAggregateRootEventPayloadConsumerDeserializer.deserialize(mock(Secret.class),
                "{\"@type\":\"TestAggregateRootEventPayload\",\"dummy\":\"dummy\"}");

        // Then
        assertEquals(new TestAggregateRootEventPayloadConsumer("dummy"), deserialized);
    }
}
