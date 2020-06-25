package com.damdamdeo.eventsourced.encryption.infra.serialization;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class JacksonBigDecimalEncryptionSerializerTest {

    @Test
    public void should_encrypt_balance_as_big_decimal() throws Exception {
        // Given
        final Secret mockSecret = mock(Secret.class);
        final Encryption mockEncryption = mock(Encryption.class);
        final ObjectWriter objectWriter = new ObjectMapper().writer()
                .withAttribute(Secret.ENCRYPTION_STRATEGY, mockEncryption)
                .withAttribute(Secret.SECRET_KEY, mockSecret);
        doReturn("BalanceEncrypted").when(mockSecret).encrypt("99.99", mockEncryption);


        final Account account = new Account("id", new BigDecimal("99.99"));

        // When
        final String serializedAccount = objectWriter.writeValueAsString(account);

        // Then
        assertEquals("{\"id\":\"id\",\"balance\":\"BalanceEncrypted\"}", serializedAccount);
        verify(mockSecret, times(1)).encrypt(any(), any());
        verifyNoMoreInteractions(mockSecret, mockEncryption);
    }

}
