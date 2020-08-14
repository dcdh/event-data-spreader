package com.damdamdeo.eventsourced.encryption.infra.jackson;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.encryption.api.NullEncryptionQualifier;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@QuarkusTest
public class JsonCryptoEncryptServiceTest {

    @Inject
    JsonCryptoService jsonCryptoService;

    @InjectMock
    SecretStore secretStore;

    @InjectMock
    @NullEncryptionQualifier
    Encryption encryption;

    @Test
    public void should_encrypt_does_not_apply_when_parent_node_is_not_an_object() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        final JsonNode parentJsonNode = mock(JsonNode.class, RETURNS_DEEP_STUBS);
        doReturn(false).when(parentJsonNode).isObject();

        // When
        jsonCryptoService.encrypt(aggregateRootId, parentJsonNode, "fieldToEncrypt", encryption);

        // Then
        verify(parentJsonNode, times(0)).get(any());
        verify(parentJsonNode, times(1)).isObject();
    }

    @Test
    public void should_encrypt_get_field_name_from_parent_node() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        final JsonNode parentJsonNode = mock(JsonNode.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(parentJsonNode).isObject();
        when(parentJsonNode.get("fieldToEncrypt").isTextual()).thenReturn(true);
        doReturn(mock(Secret.class)).when(secretStore).read(any());

        // When
        jsonCryptoService.encrypt(aggregateRootId, parentJsonNode, "fieldToEncrypt", encryption);

        // Then
        verify(parentJsonNode, atLeast(1)).get("fieldToEncrypt");
        verify(parentJsonNode, times(1)).isObject();
        verify(parentJsonNode.get("fieldToEncrypt"), times(1)).isTextual();
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_encrypt_read_aggregate_root_secret_from_secret_store() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        final JsonNode parentJsonNode = mock(JsonNode.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(parentJsonNode).isObject();
        when(parentJsonNode.get("fieldToEncrypt").isTextual()).thenReturn(true);
        doReturn(mock(Secret.class)).when(secretStore).read(any());

        // When
        jsonCryptoService.encrypt(aggregateRootId, parentJsonNode, "fieldToEncrypt", encryption);

        // Then
        verify(secretStore).read(aggregateRootId);
        verify(parentJsonNode, times(1)).isObject();
        verify(parentJsonNode, atLeast(1)).get(any());
        verify(parentJsonNode.get("fieldToEncrypt"), times(1)).isTextual();
    }

    @Test
    public void should_encrypt_encrypt_data_using_aggregate_secret() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        final JsonNode parentJsonNode = mock(JsonNode.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(parentJsonNode).isObject();
        when(parentJsonNode.get("fieldToEncrypt").isTextual()).thenReturn(true);
        when(parentJsonNode.get("fieldToEncrypt").asText()).thenReturn("valueToEncrypt");
        final Secret secret = mock(Secret.class);
        doReturn(secret).when(secretStore).read(any());

        // When
        jsonCryptoService.encrypt(aggregateRootId, parentJsonNode, "fieldToEncrypt", encryption);

        // Then
        verify(secret, times(1)).encrypt(aggregateRootId, "valueToEncrypt", encryption);
        verify(parentJsonNode, times(1)).isObject();
        verify(parentJsonNode, atLeast(1)).get(any());
        verify(parentJsonNode.get("fieldToEncrypt"), times(1)).isTextual();
        verify(parentJsonNode.get("fieldToEncrypt"), times(1)).asText();
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_encrypt_replace_content_with_encrypted_data() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        doReturn("aggregateRootType").when(aggregateRootId).aggregateRootType();
        doReturn("aggregateRootId").when(aggregateRootId).aggregateRootId();
        final ObjectNode parentJsonNode = mock(ObjectNode.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(parentJsonNode).isObject();
        when(parentJsonNode.get("fieldToEncrypt").isTextual()).thenReturn(true);
        when(parentJsonNode.get("fieldToEncrypt").asText()).thenReturn("valueToEncrypt");
        final Secret secret = mock(Secret.class);
        doReturn(secret).when(secretStore).read(any());
        doReturn("encryptedValue").when(secret).encrypt(any(), any(), any());

        // When
        jsonCryptoService.encrypt(aggregateRootId, parentJsonNode, "fieldToEncrypt", encryption);

        // Then
        final ArgumentCaptor<JsonNode> encryptedCaptor = ArgumentCaptor.forClass(JsonNode.class);
        verify(parentJsonNode, times(1)).replace(eq("fieldToEncrypt"), encryptedCaptor.capture());
        assertTrue(encryptedCaptor.getValue().has("encrypted"));
        verify(parentJsonNode, times(1)).isObject();
        verify(aggregateRootId, times(1)).aggregateRootType();
        verify(aggregateRootId, times(1)).aggregateRootId();
        verify(secret, times(1)).encrypt(any(), any(), any());
        verify(parentJsonNode, atLeast(1)).get(any());
        verify(parentJsonNode.get("fieldToEncrypt"), times(1)).isTextual();
        verify(parentJsonNode.get("fieldToEncrypt"), times(1)).asText();
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_encrypt_associate_aggregate_root_identifier_on_encrypted_value() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        doReturn("aggregateRootType").when(aggregateRootId).aggregateRootType();
        doReturn("aggregateRootId").when(aggregateRootId).aggregateRootId();
        final ObjectNode parentJsonNode = mock(ObjectNode.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(parentJsonNode).isObject();
        when(parentJsonNode.get("fieldToEncrypt").isTextual()).thenReturn(true);
        when(parentJsonNode.get("fieldToEncrypt").asText()).thenReturn("valueToEncrypt");
        final Secret secret = mock(Secret.class);
        doReturn(secret).when(secretStore).read(any());
        doReturn("encryptedValue").when(secret).encrypt(any(), any(), any());

        // When
        jsonCryptoService.encrypt(aggregateRootId, parentJsonNode, "fieldToEncrypt", encryption);

        // Then
        final ArgumentCaptor<JsonNode> encryptedCaptor = ArgumentCaptor.forClass(JsonNode.class);
        verify(parentJsonNode, times(1)).replace(eq("fieldToEncrypt"), encryptedCaptor.capture());
        assertEquals("aggregateRootType", encryptedCaptor.getValue().get("aggregateRootType").asText());
        assertEquals("aggregateRootId", encryptedCaptor.getValue().get("aggregateRootId").asText());
        verify(parentJsonNode, times(1)).isObject();
        verify(aggregateRootId, times(1)).aggregateRootType();
        verify(aggregateRootId, times(1)).aggregateRootId();
        verify(secret, times(1)).encrypt(any(), any(), any());
        verify(parentJsonNode, atLeast(1)).get(any());
        verify(parentJsonNode.get("fieldToEncrypt"), times(1)).isTextual();
        verify(parentJsonNode.get("fieldToEncrypt"), times(1)).asText();
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_encrypt_integer() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        doReturn("aggregateRootType").when(aggregateRootId).aggregateRootType();
        doReturn("aggregateRootId").when(aggregateRootId).aggregateRootId();
        final ObjectNode parentJsonNode = mock(ObjectNode.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(parentJsonNode).isObject();
        when(parentJsonNode.get("fieldToEncrypt").isInt()).thenReturn(true);
        when(parentJsonNode.get("fieldToEncrypt").asText()).thenReturn("valueToEncrypt");
        final Secret secret = mock(Secret.class);
        doReturn(secret).when(secretStore).read(any());
        doReturn("encryptedValue").when(secret).encrypt(any(), any(), any());

        // When
        jsonCryptoService.encrypt(aggregateRootId, parentJsonNode, "fieldToEncrypt", encryption);

        // Then
        final ArgumentCaptor<JsonNode> encryptedCaptor = ArgumentCaptor.forClass(JsonNode.class);
        verify(parentJsonNode, times(1)).replace(eq("fieldToEncrypt"), encryptedCaptor.capture());
        assertEquals("encryptedValue", encryptedCaptor.getValue().get("encrypted").asText());
        verify(parentJsonNode, times(1)).isObject();
        verify(aggregateRootId, times(1)).aggregateRootType();
        verify(aggregateRootId, times(1)).aggregateRootId();
        verify(secret, times(1)).encrypt(any(), any(), any());
        verify(parentJsonNode, atLeast(1)).get(any());
        verify(parentJsonNode.get("fieldToEncrypt"), times(1)).isInt();
        verify(parentJsonNode.get("fieldToEncrypt"), times(1)).asText();
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_encrypt_long() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        doReturn("aggregateRootType").when(aggregateRootId).aggregateRootType();
        doReturn("aggregateRootId").when(aggregateRootId).aggregateRootId();
        final ObjectNode parentJsonNode = mock(ObjectNode.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(parentJsonNode).isObject();
        when(parentJsonNode.get("fieldToEncrypt").isLong()).thenReturn(true);
        when(parentJsonNode.get("fieldToEncrypt").asText()).thenReturn("valueToEncrypt");
        final Secret secret = mock(Secret.class);
        doReturn(secret).when(secretStore).read(any());
        doReturn("encryptedValue").when(secret).encrypt(any(), any(), any());

        // When
        jsonCryptoService.encrypt(aggregateRootId, parentJsonNode, "fieldToEncrypt", encryption);

        // Then
        final ArgumentCaptor<JsonNode> encryptedCaptor = ArgumentCaptor.forClass(JsonNode.class);
        verify(parentJsonNode, times(1)).replace(eq("fieldToEncrypt"), encryptedCaptor.capture());
        assertEquals("encryptedValue", encryptedCaptor.getValue().get("encrypted").asText());
        verify(parentJsonNode, times(1)).isObject();
        verify(aggregateRootId, times(1)).aggregateRootType();
        verify(aggregateRootId, times(1)).aggregateRootId();
        verify(secret, times(1)).encrypt(any(), any(), any());
        verify(parentJsonNode, atLeast(1)).get(any());
        verify(parentJsonNode.get("fieldToEncrypt"), times(1)).isLong();
        verify(parentJsonNode.get("fieldToEncrypt"), times(1)).asText();
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_encrypt_bigDecimal() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        doReturn("aggregateRootType").when(aggregateRootId).aggregateRootType();
        doReturn("aggregateRootId").when(aggregateRootId).aggregateRootId();
        final ObjectNode parentJsonNode = mock(ObjectNode.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(parentJsonNode).isObject();
        when(parentJsonNode.get("fieldToEncrypt").isBigDecimal()).thenReturn(true);
        when(parentJsonNode.get("fieldToEncrypt").asText()).thenReturn("valueToEncrypt");
        final Secret secret = mock(Secret.class);
        doReturn(secret).when(secretStore).read(any());
        doReturn("encryptedValue").when(secret).encrypt(any(), any(), any());

        // When
        jsonCryptoService.encrypt(aggregateRootId, parentJsonNode, "fieldToEncrypt", encryption);

        // Then
        final ArgumentCaptor<JsonNode> encryptedCaptor = ArgumentCaptor.forClass(JsonNode.class);
        verify(parentJsonNode, times(1)).replace(eq("fieldToEncrypt"), encryptedCaptor.capture());
        assertEquals("encryptedValue", encryptedCaptor.getValue().get("encrypted").asText());
        verify(parentJsonNode, times(1)).isObject();
        verify(aggregateRootId, times(1)).aggregateRootType();
        verify(aggregateRootId, times(1)).aggregateRootId();
        verify(secret, times(1)).encrypt(any(), any(), any());
        verify(parentJsonNode, atLeast(1)).get(any());
        verify(parentJsonNode.get("fieldToEncrypt"), times(1)).isBigDecimal();
        verify(parentJsonNode.get("fieldToEncrypt"), times(1)).asText();
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_encrypt_bigInteger() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        doReturn("aggregateRootType").when(aggregateRootId).aggregateRootType();
        doReturn("aggregateRootId").when(aggregateRootId).aggregateRootId();
        final ObjectNode parentJsonNode = mock(ObjectNode.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(parentJsonNode).isObject();
        when(parentJsonNode.get("fieldToEncrypt").isBigInteger()).thenReturn(true);
        when(parentJsonNode.get("fieldToEncrypt").asText()).thenReturn("valueToEncrypt");
        final Secret secret = mock(Secret.class);
        doReturn(secret).when(secretStore).read(any());
        doReturn("encryptedValue").when(secret).encrypt(any(), any(), any());

        // When
        jsonCryptoService.encrypt(aggregateRootId, parentJsonNode, "fieldToEncrypt", encryption);

        // Then
        final ArgumentCaptor<JsonNode> encryptedCaptor = ArgumentCaptor.forClass(JsonNode.class);
        verify(parentJsonNode, times(1)).replace(eq("fieldToEncrypt"), encryptedCaptor.capture());
        assertEquals("encryptedValue", encryptedCaptor.getValue().get("encrypted").asText());
        verify(parentJsonNode, times(1)).isObject();
        verify(aggregateRootId, times(1)).aggregateRootType();
        verify(aggregateRootId, times(1)).aggregateRootId();
        verify(secret, times(1)).encrypt(any(), any(), any());
        verify(parentJsonNode, atLeast(1)).get(any());
        verify(parentJsonNode.get("fieldToEncrypt"), times(1)).isBigInteger();
        verify(parentJsonNode.get("fieldToEncrypt"), times(1)).asText();
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_encrypt_string() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        doReturn("aggregateRootType").when(aggregateRootId).aggregateRootType();
        doReturn("aggregateRootId").when(aggregateRootId).aggregateRootId();
        final ObjectNode parentJsonNode = mock(ObjectNode.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(parentJsonNode).isObject();
        when(parentJsonNode.get("fieldToEncrypt").isTextual()).thenReturn(true);
        when(parentJsonNode.get("fieldToEncrypt").asText()).thenReturn("valueToEncrypt");
        final Secret secret = mock(Secret.class);
        doReturn(secret).when(secretStore).read(any());
        doReturn("encryptedValue").when(secret).encrypt(any(), any(), any());

        // When
        jsonCryptoService.encrypt(aggregateRootId, parentJsonNode, "fieldToEncrypt", encryption);

        // Then
        final ArgumentCaptor<JsonNode> encryptedCaptor = ArgumentCaptor.forClass(JsonNode.class);
        verify(parentJsonNode, times(1)).replace(eq("fieldToEncrypt"), encryptedCaptor.capture());
        assertEquals("encryptedValue", encryptedCaptor.getValue().get("encrypted").asText());
        verify(parentJsonNode, times(1)).isObject();
        verify(aggregateRootId, times(1)).aggregateRootType();
        verify(aggregateRootId, times(1)).aggregateRootId();
        verify(secret, times(1)).encrypt(any(), any(), any());
        verify(parentJsonNode, atLeast(1)).get(any());
        verify(parentJsonNode.get("fieldToEncrypt"), times(1)).isTextual();
        verify(parentJsonNode.get("fieldToEncrypt"), times(1)).asText();
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_throw_exception_when_encrypt_unsupported_type() {
        // Given
        final JsonNode parentJsonNode = mock(JsonNode.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(parentJsonNode).isObject();

        // When && Then
        assertThrows(IllegalStateException.class, () -> jsonCryptoService.encrypt(mock(AggregateRootId.class), parentJsonNode, "fieldToEncrypt", encryption));
        verify(parentJsonNode, times(1)).isObject();
    }

}
