package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization;

import com.damdamdeo.eventsourced.encryption.api.JsonbCryptoService;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRootEventPayload;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.UnsupportedAggregateRootEventPayload;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@QuarkusTest
public class JsonbAggregateRootEventPayloadsDeSerializerTest {

     public static final class TestAggregateRootId implements AggregateRootId {

          private final String aggregateRootType;

          public TestAggregateRootId(final String aggregateRootType) {
               this.aggregateRootType = aggregateRootType;
          }

          @Override
          public String aggregateRootId() {
               return "aggregateRootId";
          }

          @Override
          public String aggregateRootType() {
               return aggregateRootType;
          }
     }

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
     public static final class TestJsonbAggregateRootEventPayloadDeSerializer implements JsonbAggregateRootEventPayloadDeSerializer {

          @Override
          public String aggregateRootType() {
               return "aggregateRootType";
          }

          @Override
          public String eventType() {
               return "eventType";
          }

          @Override
          public JsonObject encode(final AggregateRootId aggregateRootId, final AggregateRootEventPayload aggregateRootEventPayload) {
               return Json.createObjectBuilder()
                       .add("test", "test")
                       .build();
          }

          @Override
          public AggregateRootEventPayload decode(final JsonObject json) {
               return new TestAggregateRootEventPayload(json.getString("test"));
          }
     }

     @Inject
     JsonbAggregateRootEventPayloadsDeSerializer jsonbAggregateRootEventPayloadsDeSerializer;

     @InjectMock
     JsonbCryptoService jsonbCryptoService;

     @BeforeEach
     public void setup() {
          doAnswer(returnsFirstArg()).when(jsonbCryptoService).recursiveDecrypt(any());
     }

     @Test
     public void should_serialize_aggregate_root_event() {
          // Given

          // When
          final String serialized = jsonbAggregateRootEventPayloadsDeSerializer.serialize(
                  new TestAggregateRootId("aggregateRootType"),
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
                  () -> jsonbAggregateRootEventPayloadsDeSerializer.serialize(
                          new TestAggregateRootId("unknownAggregateRootType"),
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
                  () -> jsonbAggregateRootEventPayloadsDeSerializer.serialize(
                          new TestAggregateRootId("aggregateRootType"),
                          "unknownEventType",
                          mock(AggregateRootEventPayload.class)
                  ));
          assertEquals(new UnsupportedAggregateRootEventPayload("aggregateRootType", "unknownEventType"), unsupportedAggregateRootEventPayload);
     }

     @Test
     public void should_recursively_decrypt_aggregate_root_event_payload_when_deserialize_aggregate_root_event() throws Exception {
          // Given

          // When
          jsonbAggregateRootEventPayloadsDeSerializer.deserialize(
                  "aggregateRootType",
                  "eventType",
                  "{\"test\":\"test\"}"
          );

          // Then
          final ArgumentCaptor<JsonObject> sourceCaptor = ArgumentCaptor.forClass(JsonObject.class);
          verify(jsonbCryptoService, times(1)).recursiveDecrypt(sourceCaptor.capture());
          JSONAssert.assertEquals("{\"test\":\"test\"}", sourceCaptor.getValue().toString(), true);
     }

     @Test
     public void should_deserialize_aggregate_root_event() {
          // Given

          // When
          final AggregateRootEventPayload deserialized = jsonbAggregateRootEventPayloadsDeSerializer.deserialize(
                  "aggregateRootType",
                  "eventType",
                  "{\"test\":\"test\"}"
          );

          // Then
          assertEquals(new TestAggregateRootEventPayload("test"), deserialized);
//     FIXME The verify is failing, but why ???
//          verify(jsonCryptoService, times(1)).recursiveDecrypt(any(), any());
     }

     @Test
     public void should_throw_UnsupportedAggregateRootEventPayload_when_deserialize_unknown_aggregate_root_type() {
          // Given

          // When && Then
          final UnsupportedAggregateRootEventPayload unsupportedAggregateRootEventPayload = assertThrows(UnsupportedAggregateRootEventPayload.class,
                  () -> jsonbAggregateRootEventPayloadsDeSerializer.deserialize(
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
                  () -> jsonbAggregateRootEventPayloadsDeSerializer.deserialize(
                          "aggregateRootType",
                          "unknownEventType",
                          "{}"
                  ));
          assertEquals(new UnsupportedAggregateRootEventPayload("aggregateRootType", "unknownEventType"), unsupportedAggregateRootEventPayload);
     }
}
