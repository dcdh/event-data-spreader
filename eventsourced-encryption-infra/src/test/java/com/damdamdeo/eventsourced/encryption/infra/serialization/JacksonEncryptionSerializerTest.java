package com.damdamdeo.eventsourced.encryption.infra.serialization;

import com.damdamdeo.eventsourced.encryption.infra.AESEncryption;
import com.damdamdeo.eventsourced.encryption.infra.Person;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JacksonEncryptionSerializerTest {

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
    public void should_encrypt_name_when_key_is_present() throws Exception {
        // Given
        final Optional<AggregateRootSecret> secret = Optional.of(new TestAggregateRootSecret("wTObuvBniTpeYpfZfkafYqXKSugqGLWs"));
        final ObjectWriter objectWriter = new ObjectMapper().writer()
                .withAttribute(AggregateRootSecret.ENCRYPTION_STRATEGY, new AESEncryption())
                .withAttribute(AggregateRootSecret.SECRET_KEY, secret);

        final Person person = new Person("id", "Damien");

        // When
        final String serializedPerson = objectWriter.writeValueAsString(person);

        // Then
        assertEquals("{\"id\":\"id\",\"name\":\"3oihYNwwgO7qTN/Mg3ztwQ==\"}", serializedPerson);
    }

    @Test
    public void should_fail_fast_when_key_is_not_present() throws Exception {
        // Given
        final ObjectWriter objectWriter = new ObjectMapper().writer()
                .withAttribute(AggregateRootSecret.ENCRYPTION_STRATEGY, new AESEncryption())
                .withAttribute(AggregateRootSecret.SECRET_KEY, Optional.empty());

        final Person person = new Person("id", "Damien");

        // When && Then
        assertThrows(JsonMappingException.class, () -> objectWriter.writeValueAsString(person));
    }

}
