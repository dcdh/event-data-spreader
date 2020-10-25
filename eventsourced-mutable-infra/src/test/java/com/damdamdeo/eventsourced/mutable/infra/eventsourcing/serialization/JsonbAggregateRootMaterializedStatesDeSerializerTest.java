package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.model.api.AggregateRootMaterializedState;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.UnsupportedAggregateRoot;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.UnsupportedJsonbCryptoService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import org.junit.jupiter.api.Test;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@QuarkusTest
public class JsonbAggregateRootMaterializedStatesDeSerializerTest {

    public static class TestAggregateRoot extends AggregateRoot {

        public TestAggregateRoot(final String aggregateRootId) {
            super(aggregateRootId);
        }

        private TestAggregateRoot(final Builder builder) {
            super(builder.aggregateRootId,
                    builder.version);
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public static class Builder {
            private String aggregateRootId;
            private Long version;

            private Builder() {}

            public Builder withAggregateRootId(final AggregateRootId aggregateRootId) {
                this.aggregateRootId = aggregateRootId.aggregateRootId();
                return this;
            }

            public Builder withAggregateRootId(final String aggregateRootId) {
                this.aggregateRootId = aggregateRootId;
                return this;
            }

            public Builder withVersion(final Long version) {
                this.version = version;
                return this;
            }

            public TestAggregateRoot build() {
                return new TestAggregateRoot(this);
            }

        }

    }

    @ApplicationScoped
    public static class JsonbAggregateRootMaterializedStateDeSerializerTest implements JsonbAggregateRootMaterializedStateDeSerializer {

        @Override
        public String aggregateRootType() {
            return "aggregateRootType";
        }

        @Override
        public JsonObject serialize(final AggregateRoot aggregateRoot, final boolean shouldEncrypt) {
            return Json.createObjectBuilder()
                    .add("test", "test")
                    .build();
        }

        @Override
        public TestAggregateRoot deserialize(final AggregateRootId aggregateRootId, final JsonObject aggregateRoot, final Long version) {
            return TestAggregateRoot.newBuilder()
                    .withAggregateRootId(aggregateRootId)
                    .withVersion(version)
                    .build();
        }

    }

    @Inject
    JsonbAggregateRootMaterializedStatesDeSerializer jacksonAggregateRootMaterializedStatesSerializer;

    // Using @InjectMock is not working when verifying invocations ... I do not know why
    @InjectSpy
    UnsupportedJsonbCryptoService cryptoService;

    @InjectSpy
    JsonbAggregateRootMaterializedStateDeSerializerTest jacksonAggregateRootMaterializedStateDeSerializerTest;

    @Test
    public void should_serialize_aggregate_root_as_materialized_state() {
        // Given
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class);
        doReturn("aggregateRootType").when(aggregateRoot).aggregateRootType();

        // When
        final String serialized = jacksonAggregateRootMaterializedStatesSerializer.serialize(
                aggregateRoot,
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
                        false
                ));
        assertEquals(new UnsupportedAggregateRoot("unknownAggregateRootType"), unsupportedAggregateRoot);
        verify(aggregateRoot, atLeast(1)).aggregateRootType();
    }

    @Test
    public void should_deserialize_aggregate_root_from_materialized_state() throws Exception {
        // Given
        final AggregateRootMaterializedState mockAggregateRootMaterializedState = mock(AggregateRootMaterializedState.class, RETURNS_DEEP_STUBS);
        when(mockAggregateRootMaterializedState.aggregateRootId().aggregateRootId()).thenReturn("aggregateRootId");
        when(mockAggregateRootMaterializedState.aggregateRootId().aggregateRootType()).thenReturn("aggregateRootType");
        doReturn("{}").when(mockAggregateRootMaterializedState).serializedMaterializedState();
        doReturn(0L).when(mockAggregateRootMaterializedState).version();
        doAnswer(returnsFirstArg()).when(cryptoService).recursiveDecrypt(any());

        // When
        final TestAggregateRoot testAggregateRoot = jacksonAggregateRootMaterializedStatesSerializer.deserialize(mockAggregateRootMaterializedState);

        // Then
        assertEquals(TestAggregateRoot.newBuilder().withAggregateRootId("aggregateRootId").withVersion(0l).build(), testAggregateRoot);
        verify(jacksonAggregateRootMaterializedStateDeSerializerTest, times(1)).deserialize(mockAggregateRootMaterializedState.aggregateRootId(),
                Json.createObjectBuilder().build(),
                0L);
        verify(cryptoService, times(1)).recursiveDecrypt(any());
        verify(mockAggregateRootMaterializedState.aggregateRootId(), atLeastOnce()).aggregateRootId();
        verify(mockAggregateRootMaterializedState.aggregateRootId(), atLeastOnce()).aggregateRootType();
        verify(mockAggregateRootMaterializedState, atLeastOnce()).serializedMaterializedState();
        verify(mockAggregateRootMaterializedState, atLeastOnce()).version();
    }

    @Test
    public void should_throw_UnsupportedAggregateRoot_when_deserialize_unknown_aggregate_root_type() {
        // Given
        final AggregateRootMaterializedState mockAggregateRootMaterializedState = mock(AggregateRootMaterializedState.class, RETURNS_DEEP_STUBS);
        when(mockAggregateRootMaterializedState.aggregateRootId().aggregateRootType()).thenReturn("unknownAggregateRootType");

        // When && Then
        final UnsupportedAggregateRoot unsupportedAggregateRoot = assertThrows(UnsupportedAggregateRoot.class,
                () -> jacksonAggregateRootMaterializedStatesSerializer.deserialize(mockAggregateRootMaterializedState));

        assertEquals(new UnsupportedAggregateRoot("unknownAggregateRootType"), unsupportedAggregateRoot);
        verify(mockAggregateRootMaterializedState.aggregateRootId(), atLeast(1)).aggregateRootType();
    }

}
