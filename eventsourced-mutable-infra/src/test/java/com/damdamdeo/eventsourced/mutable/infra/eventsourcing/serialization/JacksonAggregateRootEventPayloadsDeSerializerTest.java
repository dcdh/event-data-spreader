package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRootEventPayload;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.UnsupportedAggregateRootEventPayload;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@QuarkusTest
public class JacksonAggregateRootEventPayloadsDeSerializerTest {

     public static final class TestAggregateRootEventPayload implements AggregateRootEventPayload {

          private final String test;

          public TestAggregateRootEventPayload(final String test) {
               this.test = test;
          }

          @Override
          public void apply(AggregateRoot aggregateRoot) {
          }

          @Override
          public boolean equals(Object o) {
               if (this == o) return true;
               if (!(o instanceof TestAggregateRootEventPayload)) return false;
               TestAggregateRootEventPayload that = (TestAggregateRootEventPayload) o;
               return Objects.equals(test, that.test);
          }

          @Override
          public int hashCode() {
               return Objects.hash(test);
          }
     }

     @ApplicationScoped
     public static final class TestJacksonAggregateRootEventPayloadDeSerializer implements JacksonAggregateRootEventPayloadDeSerializer {

          @Override
          public String aggregateRootType() {
               return "aggregateRootType";
          }

          @Override
          public String eventType() {
               return "eventType";
          }

          @Override
          public JsonNode encode(final AggregateRootEventPayload aggregateRootEventPayload, final ObjectMapper objectMapper) {
               final ObjectNode objectNode = objectMapper.createObjectNode();
               objectNode.put("test", "test");
               return objectNode;
          }

          @Override
          public AggregateRootEventPayload decode(final JsonNode json) {
               return new TestAggregateRootEventPayload(json.get("test").asText());
          }
     }

     @Inject
     JacksonAggregateRootEventPayloadsDeSerializer jacksonAggregateRootEventPayloadsDeSerializer;

     @Test
     public void should_serialize_aggregate_root_event() {
          // Given

          // When
          final String serialized = jacksonAggregateRootEventPayloadsDeSerializer.serialize(
                  "aggregateRootType",
                  "eventType",
                  new TestAggregateRootEventPayload("test")
          );

          // Then
          assertEquals("{\"test\":\"test\"}", serialized);
     }

     @Test
     public void should_throw_UnsupportedAggregateRootEventPayload_when_serialize_unknown_aggregate_root_type() {
          // Given

          // When && Then
          final UnsupportedAggregateRootEventPayload unsupportedAggregateRootEventPayload = assertThrows(UnsupportedAggregateRootEventPayload.class,
                  () -> jacksonAggregateRootEventPayloadsDeSerializer.serialize(
                          "unknownAggregateRootType",
                          "eventType",
                          mock(AggregateRootEventPayload.class)
                  ));
          assertEquals(new UnsupportedAggregateRootEventPayload("unknownAggregateRootType", "eventType"), unsupportedAggregateRootEventPayload);
     }

     @Test
     public void should_throw_UnsupportedAggregateRootEventPayload_when_serialize_unknown_event_type() {
          // Given

          // When && Then
          final UnsupportedAggregateRootEventPayload unsupportedAggregateRootEventPayload = assertThrows(UnsupportedAggregateRootEventPayload.class,
                  () -> jacksonAggregateRootEventPayloadsDeSerializer.serialize(
                          "aggregateRootType",
                          "unknownEventType",
                          mock(AggregateRootEventPayload.class)
                  ));
          assertEquals(new UnsupportedAggregateRootEventPayload("aggregateRootType", "unknownEventType"), unsupportedAggregateRootEventPayload);
     }

     @Test
     public void should_deserialize_aggregate_root_event() {
          // Given

          // When
          final AggregateRootEventPayload deserialized = jacksonAggregateRootEventPayloadsDeSerializer.deserialize(
                  "aggregateRootType",
                  "eventType",
                  "{\"test\":\"test\"}"
          );

          // Then
          assertEquals(new TestAggregateRootEventPayload("test"), deserialized);
     }

     @Test
     public void should_throw_UnsupportedAggregateRootEventPayload_when_deserialize_unknown_aggregate_root_type() {
          // Given

          // When && Then
          final UnsupportedAggregateRootEventPayload unsupportedAggregateRootEventPayload = assertThrows(UnsupportedAggregateRootEventPayload.class,
                  () -> jacksonAggregateRootEventPayloadsDeSerializer.deserialize(
                          "unknownAggregateRootType",
                          "eventType",
                          "{}"
                  ));
          assertEquals(new UnsupportedAggregateRootEventPayload("unknownAggregateRootType", "eventType"), unsupportedAggregateRootEventPayload);
     }

     @Test
     public void should_throw_UnsupportedAggregateRootEventPayload_when_deserialize_unknown_event_type() {
          // Given

          // When && Then
          final UnsupportedAggregateRootEventPayload unsupportedAggregateRootEventPayload = assertThrows(UnsupportedAggregateRootEventPayload.class,
                  () -> jacksonAggregateRootEventPayloadsDeSerializer.deserialize(
                          "aggregateRootType",
                          "unknownEventType",
                          "{}"
                  ));
          assertEquals(new UnsupportedAggregateRootEventPayload("aggregateRootType", "unknownEventType"), unsupportedAggregateRootEventPayload);
     }
}
