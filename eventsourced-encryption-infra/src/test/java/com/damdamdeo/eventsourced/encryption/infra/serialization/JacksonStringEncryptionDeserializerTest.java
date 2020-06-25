package com.damdamdeo.eventsourced.encryption.infra.serialization;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class JacksonStringEncryptionDeserializerTest {

    @Test
    public void should_decrypt_name_as_string() throws Exception {
        // Given
        final Secret mockSecret = mock(Secret.class);
        final Encryption mockEncryption = mock(Encryption.class);
        final ObjectReader objectReader = new ObjectMapper().readerFor(Person.class)
                .withAttribute(Secret.ENCRYPTION_STRATEGY, mockEncryption)
                .withAttribute(Secret.SECRET_KEY, mockSecret);
        doReturn("Damien").when(mockSecret).decrypt("DamienEncrypted", mockEncryption);

        final String serializedPerson = "{\"id\":\"id\",\"name\":\"DamienEncrypted\"}";

        // When
        final Person person = objectReader.readValue(serializedPerson);

        // Then
        assertEquals(new Person("id", "Damien"), person);
        verify(mockSecret, times(1)).decrypt(any(), any());
        verifyNoMoreInteractions(mockSecret, mockEncryption);
    }

}
