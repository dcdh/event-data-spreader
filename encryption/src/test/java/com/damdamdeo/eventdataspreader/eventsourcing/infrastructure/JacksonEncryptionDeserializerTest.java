package com.damdamdeo.eventdataspreader.eventsourcing.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JacksonEncryptionDeserializerTest {

    @Test
    public void should_decrypt_name_when_key_is_present() throws Exception {
        // Given
        final String secret = "wTObuvBniTpeYpfZfkafYqXKSugqGLWs";
        final String serializedPerson = "{\"id\":\"id\",\"name\":\"48oOMAMakA0dgr/wfajOcQ==\"}";
        final ObjectReader objectReader = new ObjectMapper().readerFor(Person.class)
                .withAttribute(JacksonEncryptionSerializer.ENCODER_SECRET, secret);

        // When
        final Person person = objectReader.readValue(serializedPerson);

        // Then
        assertEquals(new Person("id", "Damien"), person);
    }

    @Test
    public void should_anonymize_name_when_key_is_not_present() throws Exception {
        // Given
        final String serializedPerson = "{\"id\":\"id\",\"name\":\"48oOMAMakA0dgr/wfajOcQ==\"}";
        final ObjectReader objectReader = new ObjectMapper().readerFor(Person.class)
                .withAttribute(JacksonEncryptionSerializer.ENCODER_SECRET, null);

        // When
        final Person person = objectReader.readValue(serializedPerson);

        // Then
        assertEquals(new Person("id", "*****"), person);
    }

}
