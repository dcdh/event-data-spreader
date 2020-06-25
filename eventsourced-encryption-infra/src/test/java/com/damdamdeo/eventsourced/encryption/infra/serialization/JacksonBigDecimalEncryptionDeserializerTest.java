package com.damdamdeo.eventsourced.encryption.infra.serialization;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class JacksonBigDecimalEncryptionDeserializerTest {

    @Test
    public void should_decrypt_balance_as_big_decimal() throws Exception {
        // Given
        final Secret mockSecret = mock(Secret.class);
        final Encryption mockEncryption = mock(Encryption.class);
        final ObjectReader objectReader = new ObjectMapper().readerFor(Account.class)
                .withAttribute(Secret.ENCRYPTION_STRATEGY, mockEncryption)
                .withAttribute(Secret.SECRET_KEY, mockSecret);
        doReturn("99.99").when(mockSecret).decrypt("BalanceEncrypted", mockEncryption);

        final String serializedAccount = "{\"id\":\"id\",\"balance\":\"BalanceEncrypted\"}";

        // When
        final Account account = objectReader.readValue(serializedAccount);

        // Then
        assertEquals(new Account("id", new BigDecimal("99.99")), account);
        verify(mockSecret, times(1)).decrypt(any(), any());
        verifyNoMoreInteractions(mockSecret, mockEncryption);
    }

}
