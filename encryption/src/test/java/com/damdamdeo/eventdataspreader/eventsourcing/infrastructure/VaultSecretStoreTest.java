package com.damdamdeo.eventdataspreader.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;
import com.damdamdeo.eventdataspreader.eventsourcing.api.SecretStore;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class VaultSecretStoreTest {

    @Inject
    private SecretStore secretStore;

    @BeforeEach
    public void setup() {
        // a way to flush data ... I should inject VaultKvManager to write a secret
        // but currently the feature is only asked https://github.com/quarkusio/quarkus/issues/7155
        secretStore.store("aggregateRootType", "aggregateRootId", null);
    }

    @Test
    public void should_store_and_retrieve() {
        // Given
        final String secret = "Hello World";

        // When
        secretStore.store("aggregateRootType", "aggregateRootId", secret);
        final EncryptedEventSecret actualSecret = secretStore.read("aggregateRootType", "aggregateRootId").get();

        // Then
        assertEquals(new VaultEncryptedEventSecret("aggregateRootId", "aggregateRootType", "Hello World"),
                actualSecret);
    }

    @Test
    public void should_return_optional_empty_if_secret_does_not_exists() {
        assertFalse(secretStore.read("aggregateRootType", "aggregateRootId").isPresent());
    }

}
