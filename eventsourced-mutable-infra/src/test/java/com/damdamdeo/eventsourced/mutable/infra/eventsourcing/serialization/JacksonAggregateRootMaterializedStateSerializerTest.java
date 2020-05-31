package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventMetadata;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventPayload;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.spi.JacksonAggregateRootDynamicImplementation;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.spi.JacksonAggregateRootMaterializedStateImplementationDiscovery;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class JacksonAggregateRootMaterializedStateSerializerTest {

    @Inject
    JacksonAggregateRootMaterializedStateSerializer jacksonAggregateRootMaterializedStateSerializer;

    @Produces
    public JacksonAggregateRootMaterializedStateImplementationDiscovery jacksonAggregateRootMaterializedStateImplementationDiscovery() {
        return () -> singletonList(
                new JacksonAggregateRootDynamicImplementation<>(TestAggregateRoot.class, JacksonTestAggregateRootMaterializedState.class, "TestAggregateRoot"));
    }

    public static final class TestAggregateRoot extends AggregateRoot {

        private String dummy;

        public void applyCreated() {
            this.apply(new TestAggregateRootEventCreatedPayload(), Mockito.mock(AggregateRootEventMetadata.class));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestAggregateRoot that = (TestAggregateRoot) o;
            return Objects.equals(dummy, that.dummy);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dummy);
        }
    }

    public static final class TestAggregateRootEventCreatedPayload extends AggregateRootEventPayload<TestAggregateRoot> {

        @Override
        public void apply(final TestAggregateRoot aggregateRoot) {
            aggregateRoot.dummy = "dummy";
        }

        @Override
        public String eventPayloadName() {
            return "eventPayloadName";
        }

        @Override
        public AggregateRootId aggregateRootId() {
            return new AggregateRootId() {
                @Override
                public String aggregateRootId() {
                    return "dummy";
                }

                @Override
                public String aggregateRootType() {
                    return "TestAggregateRoot";
                }
            };
        }
    }

    public static abstract class JacksonTestAggregateRootMaterializedState extends JacksonAggregateRootMaterializedState {

        @JsonCreator
        public JacksonTestAggregateRootMaterializedState(@JsonProperty("aggregateRootId") final String aggregateRootId,
                                                         @JsonProperty("aggregateRootType") final String aggregateRootType,
                                                         @JsonProperty("version") final Long version,
                                                         @JsonProperty("dummy") final String dummy) {
        }

    }

    @Test
    public void should_serialize() {
        // Given
        final TestAggregateRoot testAggregateRoot = new TestAggregateRoot();
        testAggregateRoot.applyCreated();

        // When
        final String serialized = jacksonAggregateRootMaterializedStateSerializer.serialize(Optional.empty(), testAggregateRoot);

        // Then
        assertEquals("{\"@type\":\"TestAggregateRoot\",\"aggregateRootId\":\"dummy\",\"version\":0,\"aggregateRootType\":\"TestAggregateRoot\",\"dummy\":\"dummy\"}", serialized);
    }

}
