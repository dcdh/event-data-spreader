package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.UnsupportedAggregateRoot;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@QuarkusTest
public class JacksonAggregateRootMaterializedStatesSerializerTest {

    @ApplicationScoped
    public static class JacksonAggregateRootMaterializedStateSerializerTest implements JacksonAggregateRootMaterializedStateSerializer {

        @Override
        public String aggregateRootType() {
            return "aggregateRootType";
        }

        @Override
        public JsonNode encode(final AggregateRoot aggregateRoot, final Secret secret, final boolean shouldEncrypt, final ObjectMapper objectMapper) {
            final ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("test", "test");
            return objectNode;
        }
    }

    @Inject
    JacksonAggregateRootMaterializedStatesSerializer jacksonAggregateRootMaterializedStatesSerializer;

    @Test
    public void should_serialize_aggregate_root_as_materialized_state() {
        // Given
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class);
        doReturn("aggregateRootType").when(aggregateRoot).aggregateRootType();

        // When
        final String serialized = jacksonAggregateRootMaterializedStatesSerializer.serialize(
                aggregateRoot,
                mock(Secret.class),
                false
        );

        // Then
        assertEquals("{\"test\":\"test\"}", serialized);
        verify(aggregateRoot, atLeast(1)).aggregateRootType();
    }

    @Test
    public void should_throw_UnsupportedAggregateRoot_when_serialize_unknown_aggregate_root_type() {
        // Given
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class);
        doReturn("unknownAggregateRootType").when(aggregateRoot).aggregateRootType();

        // When && Then
        final UnsupportedAggregateRoot unsupportedAggregateRoot = assertThrows(UnsupportedAggregateRoot.class,
                () -> jacksonAggregateRootMaterializedStatesSerializer.serialize(
                        aggregateRoot,
                        mock(Secret.class),
                        false
                ));
        assertEquals(new UnsupportedAggregateRoot("unknownAggregateRootType"), unsupportedAggregateRoot);
        verify(aggregateRoot, atLeast(1)).aggregateRootType();
    }

}
