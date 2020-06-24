package com.damdamdeo.eventsourced.encryption.api;

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

        // When && Then
        final UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                () -> missingSecret.encrypt("String to encrypt", mockEncryption));
        assertEquals("Could not encrypt. Secret is missing", exception.getMessage());
        verifyNoMoreInteractions(mockEncryption);
    }

    @Test
    public void should_return_anonymized_value_when_decrypting() {
        // Given
        final Secret missingSecret = new MissingSecret();
        final Encryption mockEncryption = mock(Encryption.class);

        // When
        final String decrypted = missingSecret.decrypt("String to decrypt", mockEncryption);

        // Then
        assertEquals("*****", decrypted);
        verifyNoMoreInteractions(mockEncryption);
    }
}
