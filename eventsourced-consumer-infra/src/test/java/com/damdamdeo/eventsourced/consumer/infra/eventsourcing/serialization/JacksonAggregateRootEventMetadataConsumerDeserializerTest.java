package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventMetadataConsumer;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.UnsupportedAggregateRootEventMetadataConsumer;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.spi.JacksonAggregateRootEventMetadataConsumerMixInSubtypeDiscovery;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@QuarkusTest
public class JacksonAggregateRootEventMetadataConsumerDeserializerTest {

    @Inject
    JacksonAggregateRootEventMetadataConsumerDeserializer jacksonAggregateRootEventMetadataConsumerDeserializer;

    @Produces
    public JacksonAggregateRootEventMetadataConsumerMixInSubtypeDiscovery jacksonAggregateRootEventMetadataConsumerMixinSubtypeDiscovery() {
        return () -> Collections.singletonList(
                new JacksonMixInSubtype<>(TestAggregateRootEventMetadataConsumer.class, JacksonTestAggregateRootEventMetadataConsumer.class, "TestAggregateRootEventMetadata"));
    }

    public static final class TestAggregateRootEventMetadataConsumer extends AggregateRootEventMetadataConsumer {

        private final String executedBy;

        public TestAggregateRootEventMetadataConsumer(final String executedBy) {
            this.executedBy = executedBy;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestAggregateRootEventMetadataConsumer that = (TestAggregateRootEventMetadataConsumer) o;
            return Objects.equals(executedBy, that.executedBy);
        }

        @Override
        public int hashCode() {
            return Objects.hash(executedBy);
        }
    }

    public static abstract class JacksonTestAggregateRootEventMetadataConsumer extends JacksonAggregateRootEventMetadataConsumer {

        @JsonCreator
        public JacksonTestAggregateRootEventMetadataConsumer(@JsonProperty("executedBy") final String executedBy) {
        }

    }

    @Test
    public void should_deserialize() {
        // Given

        // When
        final AggregateRootEventMetadataConsumer deserialized = jacksonAggregateRootEventMetadataConsumerDeserializer.deserialize(mock(Secret.class),
                "{\"@type\":\"TestAggregateRootEventMetadata\",\"executedBy\":\"damdamdeo\"}");

        // Then
        assertEquals(new TestAggregateRootEventMetadataConsumer("damdamdeo"), deserialized);
    }

    @Test
    public void should_return_default_impl_when_deserializing_unknown_type() {
        // Given

        // When
        final AggregateRootEventMetadataConsumer deserialized = jacksonAggregateRootEventMetadataConsumerDeserializer.deserialize(mock(Secret.class),
                "{\"@type\":\"UnknownAggregateRootEventMetadata\",\"executedBy\":\"damdamdeo\"}");

        // Then
        assertTrue(deserialized instanceof UnsupportedAggregateRootEventMetadataConsumer);
    }
}
