package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.aggregaterootevent.AggregateRootEventMetadata;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.spi.JacksonAggregateRootDynamicImplementation;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.spi.JacksonAggregateRootEventMetadataImplementationDiscovery;
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
public class JacksonAggregateRootEventMetadataDeSerializerTest {

    @Inject
    JacksonAggregateRootEventMetadataDeSerializer jacksonAggregateRootEventMetadataDeSerializer;

    @Produces
    public JacksonAggregateRootEventMetadataImplementationDiscovery jacksonAggregateRootEventMetadataImplementationDiscovery() {
        return () -> Collections.singletonList(
                new JacksonAggregateRootDynamicImplementation<>(TestAggregateRootEventMetadata.class, JacksonTestAggregateRootEventMetadata.class, "TestAggregateRootEventMetadata"));
    }

    public static final class TestAggregateRootEventMetadata extends AggregateRootEventMetadata {

        private final String executedBy;

        public TestAggregateRootEventMetadata(final String executedBy) {
            this.executedBy = executedBy;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestAggregateRootEventMetadata that = (TestAggregateRootEventMetadata) o;
            return Objects.equals(executedBy, that.executedBy);
        }

        @Override
        public int hashCode() {
            return Objects.hash(executedBy);
        }
    }

    public static abstract class JacksonTestAggregateRootEventMetadata extends JacksonAggregateRootEventMetadata {

        @JsonCreator
        public JacksonTestAggregateRootEventMetadata(@JsonProperty("executedBy") final String executedBy) {
        }

    }

    @Test
    public void should_serialize() {
        // Given
        final AggregateRootEventMetadata testAggregateRootEventMetadata = new TestAggregateRootEventMetadata("damdamdeo");

        // When
        final String serialized = jacksonAggregateRootEventMetadataDeSerializer.serialize(Optional.empty(), testAggregateRootEventMetadata);

        // Then
        assertEquals("{\"@type\":\"TestAggregateRootEventMetadata\",\"executedBy\":\"damdamdeo\"}", serialized);
    }

    @Test
    public void should_deserialize() {
        // Given

        // When
        final AggregateRootEventMetadata deserialized = jacksonAggregateRootEventMetadataDeSerializer.deserialize(Optional.empty(),
                "{\"@type\":\"TestAggregateRootEventMetadata\",\"executedBy\":\"damdamdeo\"}");

        // Then
        assertEquals(new TestAggregateRootEventMetadata("damdamdeo"), deserialized);
    }

}
