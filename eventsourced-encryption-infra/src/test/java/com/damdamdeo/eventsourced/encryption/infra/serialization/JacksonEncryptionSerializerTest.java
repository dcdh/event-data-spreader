package com.damdamdeo.eventsourced.encryption.infra.serialization;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.infra.Person;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class JacksonEncryptionSerializerTest {

    @Test
    public void should_encrypt_name() throws Exception {
        // Given
        final Secret mockSecret = mock(Secret.class);
        final Encryption mockEncryption = mock(Encryption.class);
        final ObjectWriter objectWriter = new ObjectMapper().writer()
                .withAttribute(Secret.ENCRYPTION_STRATEGY, mockEncryption)
                .withAttribute(Secret.SECRET_KEY, mockSecret);
        doReturn("DamienEncrypted").when(mockSecret).encrypt("Damien", mockEncryption);

        final Person person = new Person("id", "Damien");

        // When
        final String serializedPerson = objectWriter.writeValueAsString(person);

        // Then
        assertEquals("{\"id\":\"id\",\"name\":\"DamienEncrypted\"}", serializedPerson);
        verify(mockSecret, times(1)).encrypt(any(), any());
        verifyNoMoreInteractions(mockSecret, mockEncryption);
    }

}
