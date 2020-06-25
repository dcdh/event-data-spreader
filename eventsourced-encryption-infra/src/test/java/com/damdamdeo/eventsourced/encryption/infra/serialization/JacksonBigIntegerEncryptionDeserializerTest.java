package com.damdamdeo.eventsourced.encryption.infra.serialization;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class JacksonBigIntegerEncryptionDeserializerTest {

    @Test
    public void should_decrypt_result_as_big_integer() throws Exception {
        // Given
        final Secret mockSecret = mock(Secret.class);
        final Encryption mockEncryption = mock(Encryption.class);
        final ObjectReader objectReader = new ObjectMapper().readerFor(Calculation.class)
                .withAttribute(Secret.ENCRYPTION_STRATEGY, mockEncryption)
                .withAttribute(Secret.SECRET_KEY, mockSecret);
        doReturn("99").when(mockSecret).decrypt("ResultEncrypted", mockEncryption);

        final String serializedCalculation = "{\"id\":\"id\",\"result\":\"ResultEncrypted\"}";

        // When
        final Calculation calculation = objectReader.readValue(serializedCalculation);

        // Then
        assertEquals(new Calculation("id", new BigInteger("99")), calculation);
        verify(mockSecret, times(1)).decrypt(any(), any());
        verifyNoMoreInteractions(mockSecret, mockEncryption);
    }
}
