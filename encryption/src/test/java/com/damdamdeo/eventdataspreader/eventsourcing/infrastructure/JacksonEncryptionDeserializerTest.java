package com.damdamdeo.eventdataspreader.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JacksonEncryptionDeserializerTest {

    private static final class TestEncryptedEventSecret implements EncryptedEventSecret {

        private final String secret;

        public TestEncryptedEventSecret(final String secret) {
            this.secret = secret;
        }

        @Override
        public String aggregateRootType() {
            throw new IllegalStateException("should not be called");
        }

        @Override
        public String aggregateRootId() {
            throw new IllegalStateException("should not be called");
        }

        @Override
        public String secret() {
            return secret;
        }
    }

    @Test
    public void should_decrypt_name_when_key_is_present() throws Exception {
        // Given

        final Optional<EncryptedEventSecret> secret = Optional.of(new TestEncryptedEventSecret("wTObuvBniTpeYpfZfkafYqXKSugqGLWs"));
        final String serializedPerson = "{\"id\":\"id\",\"name\":\"3oihYNwwgO7qTN/Mg3ztwQ==\"}";
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
                .withAttribute(JacksonEncryptionSerializer.ENCODER_SECRET, Optional.empty());

        // When
        final Person person = objectReader.readValue(serializedPerson);

        // Then
        assertEquals(new Person("id", "*****"), person);
    }

}
