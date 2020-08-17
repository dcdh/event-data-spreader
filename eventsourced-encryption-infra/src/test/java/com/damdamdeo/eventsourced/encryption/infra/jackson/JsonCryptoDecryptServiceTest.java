package com.damdamdeo.eventsourced.encryption.infra.jackson;

import com.damdamdeo.eventsourced.encryption.api.*;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.inject.Inject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
public class JsonCryptoDecryptServiceTest {

    @Inject
    JsonCryptoService jsonCryptoService;

    @InjectMock
    SecretStore secretStore;

    @InjectMock
    @AESEncryptionQualifier
    Encryption encryption;

    @Test
    public void should_decrypt_does_not_apply_when_parent_node_is_not_an_object() {
        // Given
        final ObjectNode jsonNode = mock(ObjectNode.class, RETURNS_DEEP_STUBS);
        doReturn(false).when(jsonNode).isObject();

        // When
        jsonCryptoService.decrypt(jsonNode, "fieldToDecrypt");

        // Then
        verify(jsonNode, times(0)).set(any(), any());
        verify(jsonNode, times(1)).isObject();
    }

    @Test
    public void should_decrypt_does_not_apply_when_target_node_is_not_an_object() {
        // Given
        final ObjectNode jsonNode = mock(ObjectNode.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(jsonNode).isObject();
        when(jsonNode.get("fieldToDecrypt").isObject()).thenReturn(false);

        // When
        jsonCryptoService.decrypt(jsonNode, "fieldToDecrypt");

        // Then
        verify(jsonNode, times(0)).set(any(), any());
        verify(jsonNode, times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).isObject();
    }

    @Test
    public void should_decrypt_on_target_object_node_having_encrypted_property() {
        // Given
        final ObjectNode jsonNode = mock(ObjectNode.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(jsonNode).isObject();
        when(jsonNode.get("fieldToDecrypt").isObject()).thenReturn(true);
        when(jsonNode.get("fieldToDecrypt").has("encrypted")).thenReturn(true);
        when(jsonNode.get("fieldToDecrypt").get("encrypted")).thenReturn(mock(JsonNode.class));
        when(jsonNode.get("fieldToDecrypt").get("aggregateRootType").asText()).thenReturn("aggregateRootType");
        when(jsonNode.get("fieldToDecrypt").get("aggregateRootId").asText()).thenReturn("aggregateRootId");
        when(jsonNode.get("fieldToDecrypt").get("type").asText()).thenReturn("string");
        final Secret secret = mock(Secret.class);
        doReturn("1664").when(secret).decrypt(any(), any(), any());
        doReturn(secret).when(secretStore).read(any());

        // When
        jsonCryptoService.decrypt(jsonNode, "fieldToDecrypt");

        // Then
        verify(jsonNode, times(1)).set(eq("fieldToDecrypt"), any());
        verify(jsonNode, times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).has(any());
        verify(jsonNode.get("fieldToDecrypt").get("aggregateRootType"), times(1)).asText();
        verify(jsonNode.get("fieldToDecrypt").get("aggregateRootId"), times(1)).asText();
        verify(jsonNode.get("fieldToDecrypt").get("type"), times(1)).asText();
        verify(secret, times(1)).decrypt(any(), any(), any());
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_decrypt_read_aggregate_root_secret_from_secret_store() {
        // Given
        final ObjectNode jsonNode = mock(ObjectNode.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(jsonNode).isObject();
        when(jsonNode.get("fieldToDecrypt").isObject()).thenReturn(true);
        when(jsonNode.get("fieldToDecrypt").has("encrypted")).thenReturn(true);
        when(jsonNode.get("fieldToDecrypt").get("encrypted")).thenReturn(mock(JsonNode.class));
        when(jsonNode.get("fieldToDecrypt").get("aggregateRootType").asText()).thenReturn("aggregateRootType");
        when(jsonNode.get("fieldToDecrypt").get("aggregateRootId").asText()).thenReturn("aggregateRootId");
        when(jsonNode.get("fieldToDecrypt").get("type").asText()).thenReturn("string");
        final Secret secret = mock(Secret.class);
        doReturn("1664").when(secret).decrypt(any(), any(), any());
        doReturn(secret).when(secretStore).read(any());

        // When
        jsonCryptoService.decrypt(jsonNode, "fieldToDecrypt");

        // Then
        verify(secretStore, times(1)).read(new JacksonAggregateRootId("aggregateRootType", "aggregateRootId"));
        verify(jsonNode, times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).has(any());
        verify(jsonNode.get("fieldToDecrypt").get("aggregateRootType"), times(1)).asText();
        verify(jsonNode.get("fieldToDecrypt").get("aggregateRootId"), times(1)).asText();
        verify(jsonNode.get("fieldToDecrypt").get("type"), times(1)).asText();
        verify(secret, times(1)).decrypt(any(), any(), any());
        verify(secretStore, times(1)).read(any());
    }

    @Test
    @Disabled
    public void should_decrypt_decrypt_data_using_aggregate_secret() {
        // Given
        final ObjectNode jsonNode = mock(ObjectNode.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(jsonNode).isObject();
        when(jsonNode.get("fieldToDecrypt").isObject()).thenReturn(true);
        when(jsonNode.get("fieldToDecrypt").has("encrypted")).thenReturn(true);
        when(jsonNode.get("fieldToDecrypt").get("encrypted").asText()).thenReturn("encryptedValue");
        when(jsonNode.get("fieldToDecrypt").get("aggregateRootType").asText()).thenReturn("aggregateRootType");
        when(jsonNode.get("fieldToDecrypt").get("aggregateRootId").asText()).thenReturn("aggregateRootId");
        when(jsonNode.get("fieldToDecrypt").get("type").asText()).thenReturn("string");
        final Secret secret = mock(Secret.class);
        doReturn("decryptedValue").when(secret).decrypt(any(), any(), any());
        doReturn(secret).when(secretStore).read(any());

        // When
        jsonCryptoService.decrypt(jsonNode, "fieldToDecrypt");

        // Then
        final ArgumentCaptor<JacksonAggregateRootId> jacksonAggregateRootIdCaptor = ArgumentCaptor.forClass(JacksonAggregateRootId.class);
//        verify(secret, times(1)).decrypt(jacksonAggregateRootIdCaptor.capture(), eq("encryptedValue"), eq(encryption)); // FIXME unable to verify using mocked injected
//        assertEquals(new JacksonAggregateRootId("aggregateRootType", "aggregateRootId"), jacksonAggregateRootIdCaptor.getValue());
        verify(jsonNode, times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).has(any());
        verify(jsonNode.get("fieldToDecrypt").get("aggregateRootType"), times(1)).asText();
        verify(jsonNode.get("fieldToDecrypt").get("aggregateRootId"), times(1)).asText();
        verify(jsonNode.get("fieldToDecrypt").get("type"), times(1)).asText();
        verify(secret, times(1)).decrypt(any(), any(), any());
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_decrypt_integer() {
        // Given
        final ObjectNode jsonNode = mock(ObjectNode.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(jsonNode).isObject();
        when(jsonNode.get("fieldToDecrypt").isObject()).thenReturn(true);
        when(jsonNode.get("fieldToDecrypt").has("encrypted")).thenReturn(true);
        when(jsonNode.get("fieldToDecrypt").get("encrypted").asText()).thenReturn("encryptedValue");
        when(jsonNode.get("fieldToDecrypt").get("aggregateRootType").asText()).thenReturn("aggregateRootType");
        when(jsonNode.get("fieldToDecrypt").get("aggregateRootId").asText()).thenReturn("aggregateRootId");
        when(jsonNode.get("fieldToDecrypt").get("type").asText()).thenReturn("integer");
        final Secret secret = mock(Secret.class);
        doReturn("1664").when(secret).decrypt(any(), any(), any());
        doReturn(secret).when(secretStore).read(any());

        // When
        jsonCryptoService.decrypt(jsonNode, "fieldToDecrypt");

        // Then
        final ArgumentCaptor<IntNode> encryptedCaptor = ArgumentCaptor.forClass(IntNode.class);
        verify(jsonNode, times(1)).set(eq("fieldToDecrypt"), encryptedCaptor.capture());
        assertEquals(1664, encryptedCaptor.getValue().asInt());
        verify(jsonNode, times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).has(any());
        verify(jsonNode.get("fieldToDecrypt").get("aggregateRootType"), times(1)).asText();
        verify(jsonNode.get("fieldToDecrypt").get("aggregateRootId"), times(1)).asText();
        verify(jsonNode.get("fieldToDecrypt").get("type"), times(1)).asText();
        verify(secret, times(1)).decrypt(any(), any(), any());
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_decrypt_long() {
        // Given
        final ObjectNode jsonNode = mock(ObjectNode.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(jsonNode).isObject();
        when(jsonNode.get("fieldToDecrypt").isObject()).thenReturn(true);
        when(jsonNode.get("fieldToDecrypt").has("encrypted")).thenReturn(true);
        when(jsonNode.get("fieldToDecrypt").get("encrypted").asText()).thenReturn("encryptedValue");
        when(jsonNode.get("fieldToDecrypt").get("aggregateRootType").asText()).thenReturn("aggregateRootType");
        when(jsonNode.get("fieldToDecrypt").get("aggregateRootId").asText()).thenReturn("aggregateRootId");
        when(jsonNode.get("fieldToDecrypt").get("type").asText()).thenReturn("long");
        final Secret secret = mock(Secret.class);
        doReturn("1664").when(secret).decrypt(any(), any(), any());
        doReturn(secret).when(secretStore).read(any());

        // When
        jsonCryptoService.decrypt(jsonNode, "fieldToDecrypt");

        // Then
        final ArgumentCaptor<LongNode> encryptedCaptor = ArgumentCaptor.forClass(LongNode.class);
        verify(jsonNode, times(1)).set(eq("fieldToDecrypt"), encryptedCaptor.capture());
        assertEquals(1664L, encryptedCaptor.getValue().asLong());
        verify(jsonNode, times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).has(any());
        verify(jsonNode.get("fieldToDecrypt").get("aggregateRootType"), times(1)).asText();
        verify(jsonNode.get("fieldToDecrypt").get("aggregateRootId"), times(1)).asText();
        verify(jsonNode.get("fieldToDecrypt").get("type"), times(1)).asText();
        verify(secret, times(1)).decrypt(any(), any(), any());
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_decrypt_bigDecimal() {
        // Given
        final ObjectNode jsonNode = mock(ObjectNode.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(jsonNode).isObject();
        when(jsonNode.get("fieldToDecrypt").isObject()).thenReturn(true);
        when(jsonNode.get("fieldToDecrypt").has("encrypted")).thenReturn(true);
        when(jsonNode.get("fieldToDecrypt").get("encrypted").asText()).thenReturn("encryptedValue");
        when(jsonNode.get("fieldToDecrypt").get("aggregateRootType").asText()).thenReturn("aggregateRootType");
        when(jsonNode.get("fieldToDecrypt").get("aggregateRootId").asText()).thenReturn("aggregateRootId");
        when(jsonNode.get("fieldToDecrypt").get("type").asText()).thenReturn("bigDecimal");
        final Secret secret = mock(Secret.class);
        doReturn("1664.51").when(secret).decrypt(any(), any(), any());
        doReturn(secret).when(secretStore).read(any());

        // When
        jsonCryptoService.decrypt(jsonNode, "fieldToDecrypt");

        // Then
        final ArgumentCaptor<DecimalNode> encryptedCaptor = ArgumentCaptor.forClass(DecimalNode.class);
        verify(jsonNode, times(1)).set(eq("fieldToDecrypt"), encryptedCaptor.capture());
        assertEquals(new BigDecimal("1664.51"), encryptedCaptor.getValue().decimalValue());
        verify(jsonNode, times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).has(any());
        verify(jsonNode.get("fieldToDecrypt").get("aggregateRootType"), times(1)).asText();
        verify(jsonNode.get("fieldToDecrypt").get("aggregateRootId"), times(1)).asText();
        verify(jsonNode.get("fieldToDecrypt").get("type"), times(1)).asText();
        verify(secret, times(1)).decrypt(any(), any(), any());
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_decrypt_bigInteger() {
        // Given
        final ObjectNode jsonNode = mock(ObjectNode.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(jsonNode).isObject();
        when(jsonNode.get("fieldToDecrypt").isObject()).thenReturn(true);
        when(jsonNode.get("fieldToDecrypt").has("encrypted")).thenReturn(true);
        when(jsonNode.get("fieldToDecrypt").get("encrypted").asText()).thenReturn("encryptedValue");
        when(jsonNode.get("fieldToDecrypt").get("aggregateRootType").asText()).thenReturn("aggregateRootType");
        when(jsonNode.get("fieldToDecrypt").get("aggregateRootId").asText()).thenReturn("aggregateRootId");
        when(jsonNode.get("fieldToDecrypt").get("type").asText()).thenReturn("bigInteger");
        final Secret secret = mock(Secret.class);
        doReturn("1664").when(secret).decrypt(any(), any(), any());
        doReturn(secret).when(secretStore).read(any());

        // When
        jsonCryptoService.decrypt(jsonNode, "fieldToDecrypt");

        // Then
        final ArgumentCaptor<BigIntegerNode> encryptedCaptor = ArgumentCaptor.forClass(BigIntegerNode.class);
        verify(jsonNode, times(1)).set(eq("fieldToDecrypt"), encryptedCaptor.capture());
        assertEquals(new BigInteger("1664"), encryptedCaptor.getValue().bigIntegerValue());
        verify(jsonNode, times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).has(any());
        verify(jsonNode.get("fieldToDecrypt").get("aggregateRootType"), times(1)).asText();
        verify(jsonNode.get("fieldToDecrypt").get("aggregateRootId"), times(1)).asText();
        verify(jsonNode.get("fieldToDecrypt").get("type"), times(1)).asText();
        verify(secret, times(1)).decrypt(any(), any(), any());
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_decrypt_string() {
        // Given
        final ObjectNode jsonNode = mock(ObjectNode.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(jsonNode).isObject();
        when(jsonNode.get("fieldToDecrypt").isObject()).thenReturn(true);
        when(jsonNode.get("fieldToDecrypt").has("encrypted")).thenReturn(true);
        when(jsonNode.get("fieldToDecrypt").get("encrypted").asText()).thenReturn("encryptedValue");
        when(jsonNode.get("fieldToDecrypt").get("aggregateRootType").asText()).thenReturn("aggregateRootType");
        when(jsonNode.get("fieldToDecrypt").get("aggregateRootId").asText()).thenReturn("aggregateRootId");
        when(jsonNode.get("fieldToDecrypt").get("type").asText()).thenReturn("string");
        final Secret secret = mock(Secret.class);
        doReturn(secret).when(secretStore).read(any());
        doReturn("1664").when(secret).decrypt(any(), any(), any());

        // When
        jsonCryptoService.decrypt(jsonNode, "fieldToDecrypt");

        // Then
        final ArgumentCaptor<TextNode> encryptedCaptor = ArgumentCaptor.forClass(TextNode.class);
        verify(jsonNode, times(1)).set(eq("fieldToDecrypt"), encryptedCaptor.capture());
        assertEquals("1664", encryptedCaptor.getValue().textValue());
        verify(jsonNode, times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).has(any());
        verify(jsonNode.get("fieldToDecrypt").get("aggregateRootType"), times(1)).asText();
        verify(jsonNode.get("fieldToDecrypt").get("aggregateRootId"), times(1)).asText();
        verify(jsonNode.get("fieldToDecrypt").get("type"), times(1)).asText();
        verify(secret, times(1)).decrypt(any(), any(), any());
        verify(secretStore, times(1)).read(any());
    }

    // anonymization
    @Test
    public void should_decrypt_integer_return_anonymized_value_when_secret_is_missing() {
        // Given
        final ObjectNode jsonNode = mock(ObjectNode.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(jsonNode).isObject();
        when(jsonNode.get("fieldToDecrypt").isObject()).thenReturn(true);
        when(jsonNode.get("fieldToDecrypt").has("encrypted")).thenReturn(true);
        when(jsonNode.get("fieldToDecrypt").get("encrypted").asText()).thenReturn("encryptedValue");
        when(jsonNode.get("fieldToDecrypt").get("aggregateRootType").asText()).thenReturn("aggregateRootType");
        when(jsonNode.get("fieldToDecrypt").get("aggregateRootId").asText()).thenReturn("aggregateRootId");
        when(jsonNode.get("fieldToDecrypt").get("type").asText()).thenReturn("integer");
        final Secret secret = mock(Secret.class);
        doReturn(secret).when(secretStore).read(any());
        doThrow(new UnableToDecryptMissingSecretException(mock(AggregateRootId.class))).when(secret).decrypt(any(), any(), any());

        // When
        jsonCryptoService.decrypt(jsonNode, "fieldToDecrypt");

        // Then
        final ArgumentCaptor<IntNode> encryptedCaptor = ArgumentCaptor.forClass(IntNode.class);
        verify(jsonNode, times(1)).set(eq("fieldToDecrypt"), encryptedCaptor.capture());
        assertEquals(0, encryptedCaptor.getValue().asInt());
        verify(jsonNode, times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).has(any());
        verify(jsonNode.get("fieldToDecrypt").get("aggregateRootType"), times(1)).asText();
        verify(jsonNode.get("fieldToDecrypt").get("aggregateRootId"), times(1)).asText();
        verify(jsonNode.get("fieldToDecrypt").get("type"), times(1)).asText();
        verify(secret, times(1)).decrypt(any(), any(), any());
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_decrypt_long_return_anonymized_value_when_secret_is_missing() {
        // Given
        final ObjectNode jsonNode = mock(ObjectNode.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(jsonNode).isObject();
        when(jsonNode.get("fieldToDecrypt").isObject()).thenReturn(true);
        when(jsonNode.get("fieldToDecrypt").has("encrypted")).thenReturn(true);
        when(jsonNode.get("fieldToDecrypt").get("encrypted").asText()).thenReturn("encryptedValue");
        when(jsonNode.get("fieldToDecrypt").get("aggregateRootType").asText()).thenReturn("aggregateRootType");
        when(jsonNode.get("fieldToDecrypt").get("aggregateRootId").asText()).thenReturn("aggregateRootId");
        when(jsonNode.get("fieldToDecrypt").get("type").asText()).thenReturn("long");
        final Secret secret = mock(Secret.class);
        doReturn(secret).when(secretStore).read(any());
        doThrow(new UnableToDecryptMissingSecretException(mock(AggregateRootId.class))).when(secret).decrypt(any(), any(), any());

        // When
        jsonCryptoService.decrypt(jsonNode, "fieldToDecrypt");

        // Then
        final ArgumentCaptor<LongNode> encryptedCaptor = ArgumentCaptor.forClass(LongNode.class);
        verify(jsonNode, times(1)).set(eq("fieldToDecrypt"), encryptedCaptor.capture());
        assertEquals(0L, encryptedCaptor.getValue().asLong());
        verify(jsonNode, times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).has(any());
        verify(jsonNode.get("fieldToDecrypt").get("aggregateRootType"), times(1)).asText();
        verify(jsonNode.get("fieldToDecrypt").get("aggregateRootId"), times(1)).asText();
        verify(jsonNode.get("fieldToDecrypt").get("type"), times(1)).asText();
        verify(secret, times(1)).decrypt(any(), any(), any());
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_decrypt_bigDecimal_return_anonymized_value_when_secret_is_missing() {
        // Given
        final ObjectNode jsonNode = mock(ObjectNode.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(jsonNode).isObject();
        when(jsonNode.get("fieldToDecrypt").isObject()).thenReturn(true);
        when(jsonNode.get("fieldToDecrypt").has("encrypted")).thenReturn(true);
        when(jsonNode.get("fieldToDecrypt").get("encrypted").asText()).thenReturn("encryptedValue");
        when(jsonNode.get("fieldToDecrypt").get("aggregateRootType").asText()).thenReturn("aggregateRootType");
        when(jsonNode.get("fieldToDecrypt").get("aggregateRootId").asText()).thenReturn("aggregateRootId");
        when(jsonNode.get("fieldToDecrypt").get("type").asText()).thenReturn("bigDecimal");
        final Secret secret = mock(Secret.class);
        doReturn(secret).when(secretStore).read(any());
        doThrow(new UnableToDecryptMissingSecretException(mock(AggregateRootId.class))).when(secret).decrypt(any(), any(), any());

        // When
        jsonCryptoService.decrypt(jsonNode, "fieldToDecrypt");

        // Then
        final ArgumentCaptor<DecimalNode> encryptedCaptor = ArgumentCaptor.forClass(DecimalNode.class);
        verify(jsonNode, times(1)).set(eq("fieldToDecrypt"), encryptedCaptor.capture());
        assertEquals(new BigDecimal("0"), encryptedCaptor.getValue().decimalValue());
        verify(jsonNode, times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).has(any());
        verify(jsonNode.get("fieldToDecrypt").get("aggregateRootType"), times(1)).asText();
        verify(jsonNode.get("fieldToDecrypt").get("aggregateRootId"), times(1)).asText();
        verify(jsonNode.get("fieldToDecrypt").get("type"), times(1)).asText();
        verify(secret, times(1)).decrypt(any(), any(), any());
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_decrypt_bigInteger_return_anonymized_value_when_secret_is_missing() {
        // Given
        final ObjectNode jsonNode = mock(ObjectNode.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(jsonNode).isObject();
        when(jsonNode.get("fieldToDecrypt").isObject()).thenReturn(true);
        when(jsonNode.get("fieldToDecrypt").has("encrypted")).thenReturn(true);
        when(jsonNode.get("fieldToDecrypt").get("encrypted").asText()).thenReturn("encryptedValue");
        when(jsonNode.get("fieldToDecrypt").get("aggregateRootType").asText()).thenReturn("aggregateRootType");
        when(jsonNode.get("fieldToDecrypt").get("aggregateRootId").asText()).thenReturn("aggregateRootId");
        when(jsonNode.get("fieldToDecrypt").get("type").asText()).thenReturn("bigInteger");
        final Secret secret = mock(Secret.class);
        doReturn(secret).when(secretStore).read(any());
        doThrow(new UnableToDecryptMissingSecretException(mock(AggregateRootId.class))).when(secret).decrypt(any(), any(), any());

        // When
        jsonCryptoService.decrypt(jsonNode, "fieldToDecrypt");

        // Then
        final ArgumentCaptor<BigIntegerNode> encryptedCaptor = ArgumentCaptor.forClass(BigIntegerNode.class);
        verify(jsonNode, times(1)).set(eq("fieldToDecrypt"), encryptedCaptor.capture());
        assertEquals(new BigInteger("0"), encryptedCaptor.getValue().bigIntegerValue());
        verify(jsonNode, times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).has(any());
        verify(jsonNode.get("fieldToDecrypt").get("aggregateRootType"), times(1)).asText();
        verify(jsonNode.get("fieldToDecrypt").get("aggregateRootId"), times(1)).asText();
        verify(jsonNode.get("fieldToDecrypt").get("type"), times(1)).asText();
        verify(secret, times(1)).decrypt(any(), any(), any());
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_decrypt_string_return_anonymized_value_when_secret_is_missing() {
        // Given
        final ObjectNode jsonNode = mock(ObjectNode.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(jsonNode).isObject();
        when(jsonNode.get("fieldToDecrypt").isObject()).thenReturn(true);
        when(jsonNode.get("fieldToDecrypt").has("encrypted")).thenReturn(true);
        when(jsonNode.get("fieldToDecrypt").get("encrypted").asText()).thenReturn("encryptedValue");
        when(jsonNode.get("fieldToDecrypt").get("aggregateRootType").asText()).thenReturn("aggregateRootType");
        when(jsonNode.get("fieldToDecrypt").get("aggregateRootId").asText()).thenReturn("aggregateRootId");
        when(jsonNode.get("fieldToDecrypt").get("type").asText()).thenReturn("string");
        final Secret secret = mock(Secret.class);
        doReturn(secret).when(secretStore).read(any());
        doThrow(new UnableToDecryptMissingSecretException(mock(AggregateRootId.class))).when(secret).decrypt(any(), any(), any());

        // When
        jsonCryptoService.decrypt(jsonNode, "fieldToDecrypt");

        // Then
        final ArgumentCaptor<TextNode> encryptedCaptor = ArgumentCaptor.forClass(TextNode.class);
        verify(jsonNode, times(1)).set(eq("fieldToDecrypt"), encryptedCaptor.capture());
        assertEquals("*****", encryptedCaptor.getValue().textValue());
        verify(jsonNode, times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).isObject();
        verify(jsonNode.get("fieldToDecrypt"), times(1)).has(any());
        verify(jsonNode.get("fieldToDecrypt").get("aggregateRootType"), times(1)).asText();
        verify(jsonNode.get("fieldToDecrypt").get("aggregateRootId"), times(1)).asText();
        verify(jsonNode.get("fieldToDecrypt").get("type"), times(1)).asText();
        verify(secret, times(1)).decrypt(any(), any(), any());
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_decrypt_recursively() throws Exception {
        // Given
        final ObjectMapper objectMapper = new ObjectMapper();
        final String givenJsonEncrypted = new Scanner(this.getClass().getResourceAsStream("/jsonEncrypted.json"), "UTF-8")
                .useDelimiter("\\A").next();
        final JsonNode jsonNode = objectMapper.readTree(givenJsonEncrypted);

        // define CarAggregateRoot Car01 decrypt behavior
        final Secret carAggregateRootCar01Secret = mock(Secret.class, RETURNS_DEEP_STUBS);
        doReturn("Damien").when(carAggregateRootCar01Secret).decrypt(eq(new JacksonAggregateRootId("CarAggregateRoot", "Car01")),
                eq("ownerAsEncryptedValue"),
                any());
        doReturn(carAggregateRootCar01Secret).when(secretStore).read(new JacksonAggregateRootId("CarAggregateRoot", "Car01"));

        // define DriverAggregateRoot Driver01 decrypt behavior
        final Secret driverAggregateRootDriver01Secret = mock(Secret.class, RETURNS_DEEP_STUBS);
        doReturn("Damien").when(driverAggregateRootDriver01Secret).decrypt(eq(new JacksonAggregateRootId("DriverAggregateRoot", "Driver01")),
                eq("driver[0]NameAsEncryptedValue"),
                any());
        doReturn("37").when(driverAggregateRootDriver01Secret).decrypt(eq(new JacksonAggregateRootId("DriverAggregateRoot", "Driver01")),
                eq("driver[0]AgeAsEncryptedValue"),
                any());
        doReturn("2000").when(driverAggregateRootDriver01Secret).decrypt(eq(new JacksonAggregateRootId("DriverAggregateRoot", "Driver01")),
                eq("driver[0]LicenseYearOfLicenceAsEncryptedValue"),
                any());
        doReturn("C").when(driverAggregateRootDriver01Secret).decrypt(eq(new JacksonAggregateRootId("DriverAggregateRoot", "Driver01")),
                eq("driver[0]LicenseCategoryAsEncryptedValue"),
                any());
        doReturn(driverAggregateRootDriver01Secret).when(secretStore).read(new JacksonAggregateRootId("DriverAggregateRoot", "Driver01"));

        // When
        jsonCryptoService.recursiveDecrypt(jsonNode);

        // Then
        final String expectedJsonDecrypted = new Scanner(this.getClass().getResourceAsStream("/jsonDecrypted.json"), "UTF-8")
                .useDelimiter("\\A").next();
        System.out.println(jsonNode);
        JSONAssert.assertEquals(expectedJsonDecrypted, jsonNode.toString(), false);

        verify(carAggregateRootCar01Secret, times(1)).decrypt(any(), any(), any());
        verify(secretStore, atLeastOnce()).read(new JacksonAggregateRootId("CarAggregateRoot", "Car01"));

        verify(driverAggregateRootDriver01Secret, times(4)).decrypt(any(), any(), any());
        verify(secretStore, atLeastOnce()).read(new JacksonAggregateRootId("DriverAggregateRoot", "Driver01"));
    }
}
