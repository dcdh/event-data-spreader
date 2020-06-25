package com.damdamdeo.eventsourced.encryption.infra.serialization;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class JacksonBigIntegerEncryptionSerializerTest {

    @Test
    public void should_encrypt_result_as_big_integer() throws Exception {
        // Given
        final Secret mockSecret = mock(Secret.class);
        final Encryption mockEncryption = mock(Encryption.class);
        final ObjectWriter objectWriter = new ObjectMapper().writer()
                .withAttribute(Secret.ENCRYPTION_STRATEGY, mockEncryption)
                .withAttribute(Secret.SECRET_KEY, mockSecret);
        doReturn("ResultEncrypted").when(mockSecret).encrypt("99", mockEncryption);


        final Calculation calculation = new Calculation("id", new BigInteger("99"));

        // When
        final String serializedCalculation = objectWriter.writeValueAsString(calculation);

        // Then
        assertEquals("{\"id\":\"id\",\"result\":\"ResultEncrypted\"}", serializedCalculation);
        verify(mockSecret, times(1)).encrypt(any(), any());
        verifyNoMoreInteractions(mockSecret, mockEncryption);
    }
}
