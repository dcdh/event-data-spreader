package com.damdamdeo.eventsourced.encryption.infra.jsonb;

import com.damdamdeo.eventsourced.encryption.api.AESEncryptionQualifier;
import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.encryption.infra.AESEncryption;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.json.JsonValue;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
public class JsonbCryptoEncryptServiceTest {

    @Inject
    JsonbCryptoService jsonbCryptoService;

    @InjectMock
    SecretStore secretStore;

    @InjectMock
    @AESEncryptionQualifier
    Encryption encryption;

    // String

    @Test
    public void should_encrypt_string_call_the_secret_store_and_the_encryption() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        doReturn("aggregateRootType").when(aggregateRootId).aggregateRootType();
        doReturn("aggregateRootId").when(aggregateRootId).aggregateRootId();
        final Secret secret = mock(Secret.class);
        doReturn("encrypted").when(secret).encrypt(any(), any(), any());
        doReturn(secret).when(secretStore).read(any());

        // When
        jsonbCryptoService.encrypt(aggregateRootId, "valueToEncrypt", true);

        // Then
        verify(secretStore, times(1)).read(aggregateRootId);
        verify(secret, times(1)).encrypt(eq(aggregateRootId), eq("valueToEncrypt"), any(AESEncryption.class));
        verify(aggregateRootId, times(1)).aggregateRootType();
        verify(aggregateRootId, times(1)).aggregateRootId();
    }

    @Test
    public void should_encrypt_string() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        doReturn("aggregateRootType").when(aggregateRootId).aggregateRootType();
        doReturn("aggregateRootId").when(aggregateRootId).aggregateRootId();
        final Secret secret = mock(Secret.class);
        doReturn("encrypted").when(secret).encrypt(any(), any(), any());
        doReturn(secret).when(secretStore).read(any());

        // When
        final JsonValue jsonValue = jsonbCryptoService.encrypt(aggregateRootId, "valueToEncrypt", true);

        // Then
        assertEquals("{\"encrypted\":\"encrypted\",\"aggregateRootType\":\"aggregateRootType\",\"aggregateRootId\":\"aggregateRootId\",\"type\":\"string\"}", jsonValue.toString());

        verify(secretStore, times(1)).read(aggregateRootId);
        verify(secret, times(1)).encrypt(any(), any(), any());
        verify(aggregateRootId, times(1)).aggregateRootType();
        verify(aggregateRootId, times(1)).aggregateRootId();
    }

    @Test
    public void should_not_encrypt_string_when_not_needed() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);

        // When
        final JsonValue jsonValue = jsonbCryptoService.encrypt(aggregateRootId, "valueToEncrypt", false);

        // Then
        assertEquals("\"valueToEncrypt\"", jsonValue.toString());
    }

    // Long

    @Test
    public void should_encrypt_long_call_the_secret_store_and_the_encryption() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        doReturn("aggregateRootType").when(aggregateRootId).aggregateRootType();
        doReturn("aggregateRootId").when(aggregateRootId).aggregateRootId();
        final Secret secret = mock(Secret.class);
        doReturn("encrypted").when(secret).encrypt(any(), any(), any());
        doReturn(secret).when(secretStore).read(any());

        // When
        jsonbCryptoService.encrypt(aggregateRootId, 0l, true);

        // Then
        verify(secretStore, times(1)).read(aggregateRootId);
        verify(secret, times(1)).encrypt(eq(aggregateRootId), eq("0"), any(AESEncryption.class));
        verify(aggregateRootId, times(1)).aggregateRootType();
        verify(aggregateRootId, times(1)).aggregateRootId();
    }

    @Test
    public void should_encrypt_long() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        doReturn("aggregateRootType").when(aggregateRootId).aggregateRootType();
        doReturn("aggregateRootId").when(aggregateRootId).aggregateRootId();
        final Secret secret = mock(Secret.class);
        doReturn("encrypted").when(secret).encrypt(any(), any(), any());
        doReturn(secret).when(secretStore).read(any());

        // When
        final JsonValue jsonValue = jsonbCryptoService.encrypt(aggregateRootId, 0l, true);

        // Then
        assertEquals("{\"encrypted\":\"encrypted\",\"aggregateRootType\":\"aggregateRootType\",\"aggregateRootId\":\"aggregateRootId\",\"type\":\"long\"}", jsonValue.toString());

        verify(secretStore, times(1)).read(aggregateRootId);
        verify(secret, times(1)).encrypt(any(), any(), any());
        verify(aggregateRootId, times(1)).aggregateRootType();
        verify(aggregateRootId, times(1)).aggregateRootId();
    }

    @Test
    public void should_not_encrypt_long_when_not_needed() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);

        // When
        final JsonValue jsonValue = jsonbCryptoService.encrypt(aggregateRootId, 0l, false);

        // Then
        assertEquals("0", jsonValue.toString());
    }

    // Integer

    @Test
    public void should_encrypt_integer_call_the_secret_store_and_the_encryption() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        doReturn("aggregateRootType").when(aggregateRootId).aggregateRootType();
        doReturn("aggregateRootId").when(aggregateRootId).aggregateRootId();
        final Secret secret = mock(Secret.class);
        doReturn("encrypted").when(secret).encrypt(any(), any(), any());
        doReturn(secret).when(secretStore).read(any());

        // When
        jsonbCryptoService.encrypt(aggregateRootId, 0, true);

        // Then
        verify(secretStore, times(1)).read(aggregateRootId);
        verify(secret, times(1)).encrypt(eq(aggregateRootId), eq("0"), any(AESEncryption.class));
        verify(aggregateRootId, times(1)).aggregateRootType();
        verify(aggregateRootId, times(1)).aggregateRootId();
    }

    @Test
    public void should_encrypt_integer() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        doReturn("aggregateRootType").when(aggregateRootId).aggregateRootType();
        doReturn("aggregateRootId").when(aggregateRootId).aggregateRootId();
        final Secret secret = mock(Secret.class);
        doReturn("encrypted").when(secret).encrypt(any(), any(), any());
        doReturn(secret).when(secretStore).read(any());

        // When
        final JsonValue jsonValue = jsonbCryptoService.encrypt(aggregateRootId, 0, true);

        // Then
        assertEquals("{\"encrypted\":\"encrypted\",\"aggregateRootType\":\"aggregateRootType\",\"aggregateRootId\":\"aggregateRootId\",\"type\":\"integer\"}", jsonValue.toString());

        verify(secretStore, times(1)).read(aggregateRootId);
        verify(secret, times(1)).encrypt(any(), any(), any());
        verify(aggregateRootId, times(1)).aggregateRootType();
        verify(aggregateRootId, times(1)).aggregateRootId();
    }

    @Test
    public void should_not_encrypt_integer_when_not_needed() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);

        // When
        final JsonValue jsonValue = jsonbCryptoService.encrypt(aggregateRootId, 0, false);

        // Then
        assertEquals("0", jsonValue.toString());
    }

    // BigInteger

    @Test
    public void should_encrypt_big_integer_call_the_secret_store_and_the_encryption() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        doReturn("aggregateRootType").when(aggregateRootId).aggregateRootType();
        doReturn("aggregateRootId").when(aggregateRootId).aggregateRootId();
        final Secret secret = mock(Secret.class);
        doReturn("encrypted").when(secret).encrypt(any(), any(), any());
        doReturn(secret).when(secretStore).read(any());

        // When
        jsonbCryptoService.encrypt(aggregateRootId, BigInteger.ZERO, true);

        // Then
        verify(secretStore, times(1)).read(aggregateRootId);
        verify(secret, times(1)).encrypt(eq(aggregateRootId), eq("0"), any(AESEncryption.class));
        verify(aggregateRootId, times(1)).aggregateRootType();
        verify(aggregateRootId, times(1)).aggregateRootId();
    }

    @Test
    public void should_encrypt_big_integer() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        doReturn("aggregateRootType").when(aggregateRootId).aggregateRootType();
        doReturn("aggregateRootId").when(aggregateRootId).aggregateRootId();
        final Secret secret = mock(Secret.class);
        doReturn("encrypted").when(secret).encrypt(any(), any(), any());
        doReturn(secret).when(secretStore).read(any());

        // When
        final JsonValue jsonValue = jsonbCryptoService.encrypt(aggregateRootId, BigInteger.ZERO, true);

        // Then
        assertEquals("{\"encrypted\":\"encrypted\",\"aggregateRootType\":\"aggregateRootType\",\"aggregateRootId\":\"aggregateRootId\",\"type\":\"bigInteger\"}", jsonValue.toString());

        verify(secretStore, times(1)).read(aggregateRootId);
        verify(secret, times(1)).encrypt(any(), any(), any());
        verify(aggregateRootId, times(1)).aggregateRootType();
        verify(aggregateRootId, times(1)).aggregateRootId();
    }

    @Test
    public void should_not_encrypt_big_integer_when_not_needed() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);

        // When
        final JsonValue jsonValue = jsonbCryptoService.encrypt(aggregateRootId, BigInteger.ZERO, false);

        // Then
        assertEquals("0", jsonValue.toString());
    }

    // BigDecimal

    @Test
    public void should_encrypt_big_decimal_call_the_secret_store_and_the_encryption() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        doReturn("aggregateRootType").when(aggregateRootId).aggregateRootType();
        doReturn("aggregateRootId").when(aggregateRootId).aggregateRootId();
        final Secret secret = mock(Secret.class);
        doReturn("encrypted").when(secret).encrypt(any(), any(), any());
        doReturn(secret).when(secretStore).read(any());

        // When
        jsonbCryptoService.encrypt(aggregateRootId, BigDecimal.ZERO, true);

        // Then
        verify(secretStore, times(1)).read(aggregateRootId);
        verify(secret, times(1)).encrypt(eq(aggregateRootId), eq("0"), any(AESEncryption.class));
        verify(aggregateRootId, times(1)).aggregateRootType();
        verify(aggregateRootId, times(1)).aggregateRootId();
    }

    @Test
    public void should_encrypt_big_decimal() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        doReturn("aggregateRootType").when(aggregateRootId).aggregateRootType();
        doReturn("aggregateRootId").when(aggregateRootId).aggregateRootId();
        final Secret secret = mock(Secret.class);
        doReturn("encrypted").when(secret).encrypt(any(), any(), any());
        doReturn(secret).when(secretStore).read(any());

        // When
        final JsonValue jsonValue = jsonbCryptoService.encrypt(aggregateRootId, BigDecimal.ZERO, true);

        // Then
        assertEquals("{\"encrypted\":\"encrypted\",\"aggregateRootType\":\"aggregateRootType\",\"aggregateRootId\":\"aggregateRootId\",\"type\":\"bigDecimal\"}", jsonValue.toString());

        verify(secretStore, times(1)).read(aggregateRootId);
        verify(secret, times(1)).encrypt(any(), any(), any());
        verify(aggregateRootId, times(1)).aggregateRootType();
        verify(aggregateRootId, times(1)).aggregateRootId();
    }

    @Test
    public void should_not_encrypt_big_decimal_when_not_needed() {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);

        // When
        final JsonValue jsonValue = jsonbCryptoService.encrypt(aggregateRootId, BigDecimal.ZERO, false);

        // Then
        assertEquals("0", jsonValue.toString());
    }

}
