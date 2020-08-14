package com.damdamdeo.eventsourced.encryption.api;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class MissingSecretTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(MissingSecret.class).verify();
    }

    @Test
    public void should_throw_exception_when_getting_secret() {
        // Given
        final Secret missingSecret = new MissingSecret();

        // When && Then
        final UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> missingSecret.secret());
        assertEquals("Secret is missing", exception.getMessage());
    }

    @Test
    public void should_throw_exception_when_encrypting() {
        // Given
        final Secret missingSecret = new MissingSecret();
        final Encryption mockEncryption = mock(Encryption.class);
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);

        // When && Then
        final UnableToEncryptMissingSecretException exception = assertThrows(UnableToEncryptMissingSecretException.class,
                () -> missingSecret.encrypt(aggregateRootId, "String to encrypt", mockEncryption));
        assertEquals(aggregateRootId, exception.aggregateRootId());
        verifyNoMoreInteractions(mockEncryption);
    }

    @Test
    public void should_throw_exception_when_decrypting() {
        // Given
        final Secret missingSecret = new MissingSecret();
        final Encryption mockEncryption = mock(Encryption.class);
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);

        // When && Then
        final UnableToDecryptMissingSecretException exception = assertThrows(UnableToDecryptMissingSecretException.class,
                () -> missingSecret.decrypt(aggregateRootId, "String to decrypt", mockEncryption));
        assertEquals(aggregateRootId, exception.aggregateRootId());
        verifyNoMoreInteractions(mockEncryption);
    }
}
