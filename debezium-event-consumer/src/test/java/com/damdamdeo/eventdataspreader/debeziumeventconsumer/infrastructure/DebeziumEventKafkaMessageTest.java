package com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure;

import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import io.vertx.core.json.JsonObject;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DebeziumEventKafkaMessageTest {

    @Test
    public void should_be_equals() {
        EqualsVerifier.forClass(DebeziumEventKafkaMessage.class).verify();
    }

    @Test
    public void should_fail_fast_when_message_key_is_missing() {
        // Given
        final KafkaMessage<JsonObject, JsonObject> message = mock(KafkaMessage.class);

        // When && Then
        final Throwable throwable = assertThrows(UnableToDecodeDebeziumEventMessageException.class,
                () -> new DebeziumEventKafkaMessage(message));
        assertEquals("'Message Key' is missing", throwable.getMessage());
        verify(message).getKey();
    }

    @Test
    public void should_fail_fast_when_message_key_payload_is_missing() {
        // Given
        final KafkaMessage<JsonObject, JsonObject> message = mock(KafkaMessage.class, RETURNS_DEEP_STUBS);
        when(message.getKey()).thenReturn(mock(JsonObject.class));
        when(message.getKey().getJsonObject("payload")).thenReturn(null);

        // When && Then
        final Throwable throwable = assertThrows(UnableToDecodeDebeziumEventMessageException.class,
                () -> new DebeziumEventKafkaMessage(message));
        assertEquals("Message Key 'payload' is missing", throwable.getMessage());
        verify(message, atLeastOnce()).getKey();
        verify(message.getKey(), atLeastOnce()).getJsonObject("payload");
    }

    @Test
    public void should_fail_fast_when_message_key_payload_eventId_is_missing() {
        // Given
        final KafkaMessage<JsonObject, JsonObject> message = mock(KafkaMessage.class, RETURNS_DEEP_STUBS);
        when(message.getKey()).thenReturn(mock(JsonObject.class));
        when(message.getKey().getJsonObject("payload")).thenReturn(mock(JsonObject.class));
        when(message.getKey().getJsonObject("payload").getString("eventid")).thenReturn(null);

        // When && Then
        final Throwable throwable = assertThrows(UnableToDecodeDebeziumEventMessageException.class,
                () -> new DebeziumEventKafkaMessage(message));
        assertEquals("Message Key payload 'event_id' is missing", throwable.getMessage());
        verify(message, atLeastOnce()).getKey();
        verify(message.getKey(), atLeastOnce()).getJsonObject("payload");
        verify(message.getKey().getJsonObject("payload"), atLeastOnce()).getString("eventid");
    }

    @Test
    public void should_fail_fast_when_message_payload_is_missing() {
        // Given
        final KafkaMessage<JsonObject, JsonObject> message = mock(KafkaMessage.class, RETURNS_DEEP_STUBS);
        when(message.getKey()).thenReturn(mock(JsonObject.class));
        when(message.getKey().getJsonObject("payload")).thenReturn(mock(JsonObject.class));
        when(message.getKey().getJsonObject("payload").getString("eventid")).thenReturn("eventid");
        when(message.getPayload()).thenReturn(null);

        // When && Then
        final Throwable throwable = assertThrows(UnableToDecodeDebeziumEventMessageException.class,
                () -> new DebeziumEventKafkaMessage(message));
        assertEquals("'Message Payload' is missing", throwable.getMessage());
        verify(message, atLeastOnce()).getKey();
        verify(message.getKey(), atLeastOnce()).getJsonObject("payload");
        verify(message.getKey().getJsonObject("payload"), atLeastOnce()).getString("eventid");
        verify(message, atLeastOnce()).getPayload();
    }

    @Test
    public void should_fail_fast_when_message_payload_payload_is_missing() {
        // Given
        final KafkaMessage<JsonObject, JsonObject> message = mock(KafkaMessage.class, RETURNS_DEEP_STUBS);
        when(message.getKey()).thenReturn(mock(JsonObject.class));
        when(message.getKey().getJsonObject("payload")).thenReturn(mock(JsonObject.class));
        when(message.getKey().getJsonObject("payload").getString("eventid")).thenReturn("eventid");
        when(message.getPayload()).thenReturn(mock(JsonObject.class));
        when(message.getPayload().getJsonObject("payload")).thenReturn(null);

        // When && Then
        final Throwable throwable = assertThrows(UnableToDecodeDebeziumEventMessageException.class,
                () -> new DebeziumEventKafkaMessage(message));
        assertEquals("Message Payload 'payload' is missing", throwable.getMessage());
        verify(message, atLeastOnce()).getKey();
        verify(message.getKey(), atLeastOnce()).getJsonObject("payload");
        verify(message.getKey().getJsonObject("payload"), atLeastOnce()).getString("eventid");
        verify(message, atLeastOnce()).getPayload();
        verify(message.getPayload(), atLeastOnce()).getJsonObject("payload");
    }

    @Test
    public void should_fail_fast_when_message_payload_payload_after_is_missing() {
        // Given
        final KafkaMessage<JsonObject, JsonObject> message = mock(KafkaMessage.class, RETURNS_DEEP_STUBS);
        when(message.getKey()).thenReturn(mock(JsonObject.class));
        when(message.getKey().getJsonObject("payload")).thenReturn(mock(JsonObject.class));
        when(message.getKey().getJsonObject("payload").getString("eventid")).thenReturn("eventid");
        when(message.getPayload()).thenReturn(mock(JsonObject.class));
        when(message.getPayload().getJsonObject("payload")).thenReturn(mock(JsonObject.class));
        when(message.getPayload().getJsonObject("payload").getJsonObject("after")).thenReturn(null);

        // When && Then
        final Throwable throwable = assertThrows(UnableToDecodeDebeziumEventMessageException.class,
                () -> new DebeziumEventKafkaMessage(message));
        assertEquals("'after' is missing", throwable.getMessage());
        verify(message, atLeastOnce()).getKey();
        verify(message.getKey(), atLeastOnce()).getJsonObject("payload");
        verify(message.getKey().getJsonObject("payload"), atLeastOnce()).getString("eventid");
        verify(message, atLeastOnce()).getPayload();
        verify(message.getPayload(), atLeastOnce()).getJsonObject("payload");
        verify(message.getPayload().getJsonObject("payload"), atLeastOnce()).getJsonObject("after");
    }

    @Test
    public void should_fail_fast_when_message_payload_payload_operation_is_missing() {
        // Given
        final KafkaMessage<JsonObject, JsonObject> message = mock(KafkaMessage.class, RETURNS_DEEP_STUBS);
        when(message.getKey()).thenReturn(mock(JsonObject.class));
        when(message.getKey().getJsonObject("payload")).thenReturn(mock(JsonObject.class));
        when(message.getKey().getJsonObject("payload").getString("eventid")).thenReturn("eventid");
        when(message.getPayload()).thenReturn(mock(JsonObject.class));
        when(message.getPayload().getJsonObject("payload")).thenReturn(mock(JsonObject.class));
        when(message.getPayload().getJsonObject("payload").getJsonObject("after")).thenReturn(mock(JsonObject.class));
        when(message.getPayload().getJsonObject("payload").getString("op")).thenReturn(null);

        // When && Then
        final Throwable throwable = assertThrows(UnableToDecodeDebeziumEventMessageException.class,
                () -> new DebeziumEventKafkaMessage(message));
        assertEquals("'op' is missing", throwable.getMessage());
        verify(message, atLeastOnce()).getKey();
        verify(message.getKey(), atLeastOnce()).getJsonObject("payload");
        verify(message.getKey().getJsonObject("payload"), atLeastOnce()).getString("eventid");
        verify(message, atLeastOnce()).getPayload();
        verify(message.getPayload(), atLeastOnce()).getJsonObject("payload");
        verify(message.getPayload().getJsonObject("payload"), atLeastOnce()).getJsonObject("after");
        verify(message.getPayload().getJsonObject("payload"), atLeastOnce()).getString("op");
    }

    @Test
    public void should_fail_fast_when_message_payload_payload_operation_is_deleted() {
        // Given
        final KafkaMessage<JsonObject, JsonObject> message = mock(KafkaMessage.class, RETURNS_DEEP_STUBS);
        when(message.getKey()).thenReturn(mock(JsonObject.class));
        when(message.getKey().getJsonObject("payload")).thenReturn(mock(JsonObject.class));
        when(message.getKey().getJsonObject("payload").getString("eventid")).thenReturn("eventid");
        when(message.getPayload()).thenReturn(mock(JsonObject.class));
        when(message.getPayload().getJsonObject("payload")).thenReturn(mock(JsonObject.class));
        when(message.getPayload().getJsonObject("payload").getJsonObject("after")).thenReturn(mock(JsonObject.class));
        when(message.getPayload().getJsonObject("payload").getString("op")).thenReturn("d");

        // When && Then
        final Throwable throwable = assertThrows(UnableToDecodeDebeziumEventMessageException.class,
                () -> new DebeziumEventKafkaMessage(message));
        assertEquals("only debezium create or read operation - data inserted in database - are supported", throwable.getMessage());
        verify(message, atLeastOnce()).getKey();
        verify(message.getKey(), atLeastOnce()).getJsonObject("payload");
        verify(message.getKey().getJsonObject("payload"), atLeastOnce()).getString("eventid");
        verify(message, atLeastOnce()).getPayload();
        verify(message.getPayload(), atLeastOnce()).getJsonObject("payload");
        verify(message.getPayload().getJsonObject("payload"), atLeastOnce()).getJsonObject("after");
        verify(message.getPayload().getJsonObject("payload"), atLeastOnce()).getString("op");
    }

    @Test
    public void should_fail_fast_when_message_payload_payload_operation_is_updated() {
        // Given
        final KafkaMessage<JsonObject, JsonObject> message = mock(KafkaMessage.class, RETURNS_DEEP_STUBS);
        when(message.getKey()).thenReturn(mock(JsonObject.class));
        when(message.getKey().getJsonObject("payload")).thenReturn(mock(JsonObject.class));
        when(message.getKey().getJsonObject("payload").getString("eventid")).thenReturn("eventid");
        when(message.getPayload()).thenReturn(mock(JsonObject.class));
        when(message.getPayload().getJsonObject("payload")).thenReturn(mock(JsonObject.class));
        when(message.getPayload().getJsonObject("payload").getJsonObject("after")).thenReturn(mock(JsonObject.class));
        when(message.getPayload().getJsonObject("payload").getString("op")).thenReturn("u");

        // When && Then
        final Throwable throwable = assertThrows(UnableToDecodeDebeziumEventMessageException.class,
                () -> new DebeziumEventKafkaMessage(message));
        assertEquals("only debezium create or read operation - data inserted in database - are supported", throwable.getMessage());
        verify(message, atLeastOnce()).getKey();
        verify(message.getKey(), atLeastOnce()).getJsonObject("payload");
        verify(message.getKey().getJsonObject("payload"), atLeastOnce()).getString("eventid");
        verify(message, atLeastOnce()).getPayload();
        verify(message.getPayload(), atLeastOnce()).getJsonObject("payload");
        verify(message.getPayload().getJsonObject("payload"), atLeastOnce()).getJsonObject("after");
        verify(message.getPayload().getJsonObject("payload"), atLeastOnce()).getString("op");
    }

    @Test
    public void should_fail_fast_when_eventids_mismatched() {
        // Given
        final KafkaMessage<JsonObject, JsonObject> message = mock(KafkaMessage.class, RETURNS_DEEP_STUBS);
        when(message.getKey()).thenReturn(mock(JsonObject.class));
        when(message.getKey().getJsonObject("payload")).thenReturn(mock(JsonObject.class));
        when(message.getKey().getJsonObject("payload").getString("eventid")).thenReturn("0123456789");
        when(message.getPayload()).thenReturn(mock(JsonObject.class));
        when(message.getPayload().getJsonObject("payload")).thenReturn(mock(JsonObject.class));
        when(message.getPayload().getJsonObject("payload").getJsonObject("after")).thenReturn(mock(JsonObject.class));
        when(message.getPayload().getJsonObject("payload").getJsonObject("after").getString("eventid")).thenReturn("azerty");
        when(message.getPayload().getJsonObject("payload").getString("op")).thenReturn("c");

        // When && Then
        final Throwable throwable = assertThrows(UnableToDecodeDebeziumEventMessageException.class,
                () -> new DebeziumEventKafkaMessage(message));
        assertEquals("events mismatch", throwable.getMessage());
        verify(message, atLeastOnce()).getKey();
        verify(message.getKey(), atLeastOnce()).getJsonObject("payload");
        verify(message.getKey().getJsonObject("payload"), atLeastOnce()).getString("eventid");
        verify(message, atLeastOnce()).getPayload();
        verify(message.getPayload(), atLeastOnce()).getJsonObject("payload");
        verify(message.getPayload().getJsonObject("payload"), atLeastOnce()).getJsonObject("after");
        verify(message.getPayload().getJsonObject("payload").getJsonObject("after"), atLeastOnce()).getString("eventid");
        verify(message.getPayload().getJsonObject("payload"), atLeastOnce()).getString("op");
    }

}
