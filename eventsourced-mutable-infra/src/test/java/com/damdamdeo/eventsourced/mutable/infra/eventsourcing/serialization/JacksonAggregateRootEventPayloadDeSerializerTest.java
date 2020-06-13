package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventPayload;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.spi.JacksonAggregateRootEventPayloadMixInSubtypeDiscovery;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.spi.JacksonMixInSubtype;
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
public class JacksonAggregateRootEventPayloadDeSerializerTest {

    @Inject
    JacksonAggregateRootEventPayloadDeSerializer jacksonAggregateRootEventPayloadDeSerializer;

    @Produces
    public JacksonAggregateRootEventPayloadMixInSubtypeDiscovery jacksonAggregateRootEventPayloadMixInSubtypeDiscovery() {
        return () -> Collections.singletonList(
                new JacksonMixInSubtype<>(TestAggregateRootEventPayload.class, JacksonTestAggregateRootEventPayload.class, "TestAggregateRootEventPayload"));
    }

    public static final class TestAggregateRootEventPayload extends AggregateRootEventPayload {

        private final String dummy;

        public TestAggregateRootEventPayload(final String dummy) {
            this.dummy = Objects.requireNonNull(dummy);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestAggregateRootEventPayload that = (TestAggregateRootEventPayload) o;
            return Objects.equals(dummy, that.dummy);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dummy);
        }

        @Override
        public void apply(AggregateRoot aggregateRoot) {

        }

        @Override
        public String eventPayloadName() {
            return null;
        }

        @Override
        public AggregateRootId aggregateRootId() {
            return null;
        }
    }

    public static abstract class JacksonTestAggregateRootEventPayload extends JacksonAggregateRootEventPayload {

        @JsonCreator
        public JacksonTestAggregateRootEventPayload(@JsonProperty("dummy") final String dummy) {
        }

    }

    @Test
    public void should_serialize() {
        // Given
        final AggregateRootEventPayload testAggregateRootEventPayload = new TestAggregateRootEventPayload("dummy");

        // When
        final String serialized = jacksonAggregateRootEventPayloadDeSerializer.serialize(Optional.empty(), testAggregateRootEventPayload);

        // Then
        assertEquals("{\"@type\":\"TestAggregateRootEventPayload\",\"dummy\":\"dummy\"}", serialized);
    }

    @Test
    public void should_deserialize() {
        // Given

        // When
        final AggregateRootEventPayload deserialized = jacksonAggregateRootEventPayloadDeSerializer.deserialize(Optional.empty(),
                "{\"@type\":\"TestAggregateRootEventPayload\",\"dummy\":\"dummy\"}");

        // Then
        assertEquals(new TestAggregateRootEventPayload("dummy"), deserialized);
    }
}
