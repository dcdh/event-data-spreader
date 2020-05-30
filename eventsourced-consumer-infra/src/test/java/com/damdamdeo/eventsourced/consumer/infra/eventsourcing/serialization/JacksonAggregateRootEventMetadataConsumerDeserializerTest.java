package com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventMetadataConsumer;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.spi.JacksonAggregateRootEventMetadataConsumerAggregateRootImplementationDiscovery;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.spi.JacksonAggregateRootDynamicImplementation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class JacksonAggregateRootEventMetadataConsumerDeserializerTest {

    @Inject
    JacksonAggregateRootEventMetadataConsumerDeserializer jacksonAggregateRootEventMetadataConsumerDeserializer;

    @Produces
    public JacksonAggregateRootEventMetadataConsumerAggregateRootImplementationDiscovery jacksonAggregateRootEventMetadataConsumerImplementationDiscovery() {
        return () -> Collections.singletonList(
                new JacksonAggregateRootDynamicImplementation<>(TestAggregateRootEventMetadataConsumer.class, JacksonTestAggregateRootEventMetadataConsumer.class, "TestAggregateRootEventMetadata"));
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
        final AggregateRootEventMetadataConsumer deserialized = jacksonAggregateRootEventMetadataConsumerDeserializer.deserialize(Optional.empty(),
                "{\"@type\":\"TestAggregateRootEventMetadata\",\"executedBy\":\"damdamdeo\"}");

        // Then
        assertEquals(new TestAggregateRootEventMetadataConsumer("damdamdeo"), deserialized);
    }

}
