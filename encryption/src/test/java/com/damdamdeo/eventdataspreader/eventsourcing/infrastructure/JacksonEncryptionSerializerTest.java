package com.damdamdeo.eventdataspreader.eventsourcing.infrastructure;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JacksonEncryptionSerializerTest {

    @Test
    public void should_encrypt_name_when_key_is_present() throws Exception {
        // Given
        final String secret = "wTObuvBniTpeYpfZfkafYqXKSugqGLWs";
        final ObjectWriter objectWriter = new ObjectMapper().writer().withAttribute(JacksonEncryptionSerializer.ENCODER_SECRET, secret);

        final Person person = new Person("id", "Damien");

        // When
        final String serializedPerson = objectWriter.writeValueAsString(person);

        // Then
        assertEquals("{\"id\":\"id\",\"name\":\"48oOMAMakA0dgr/wfajOcQ==\"}", serializedPerson);
    }

    @Test
    public void should_fail_fast_when_key_is_not_present() throws Exception {
        // Given
        final ObjectWriter objectWriter = new ObjectMapper().writer().withAttribute(JacksonEncryptionSerializer.ENCODER_SECRET, null);

        final Person person = new Person("id", "Damien");

        // When && Then
        assertThrows(JsonMappingException.class, () -> objectWriter.writeValueAsString(person));
    }

}
