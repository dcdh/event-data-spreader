package com.damdamdeo.eventsourced.encryption.infra.serialization;

import com.damdamdeo.eventsourced.encryption.infra.AESEncryption;
import com.damdamdeo.eventsourced.encryption.infra.Person;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JacksonEncryptionDeserializerTest {

    private static final class TestAggregateRootSecret implements AggregateRootSecret {

        private final String secret;

        public TestAggregateRootSecret(final String secret) {
            this.secret = secret;
        }

        @Override
        public AggregateRootId aggregateRootId() {
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

        final Optional<AggregateRootSecret> secret = Optional.of(new TestAggregateRootSecret("wTObuvBniTpeYpfZfkafYqXKSugqGLWs"));
        final String serializedPerson = "{\"id\":\"id\",\"name\":\"3oihYNwwgO7qTN/Mg3ztwQ==\"}";
        final ObjectReader objectReader = new ObjectMapper().readerFor(Person.class)
                .withAttribute(AggregateRootSecret.ENCRYPTION_STRATEGY, new AESEncryption())
                .withAttribute(AggregateRootSecret.SECRET_KEY, secret);

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
                .withAttribute(AggregateRootSecret.ENCRYPTION_STRATEGY, new AESEncryption())
                .withAttribute(AggregateRootSecret.SECRET_KEY, Optional.empty());

        // When
        final Person person = objectReader.readValue(serializedPerson);

        // Then
        assertEquals(new Person("id", "*****"), person);
    }

}
