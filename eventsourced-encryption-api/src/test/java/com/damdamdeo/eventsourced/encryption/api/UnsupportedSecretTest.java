package com.damdamdeo.eventsourced.encryption.api;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class UnsupportedSecretTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(UnsupportedSecret.class).verify();
    }

    @Test
    public void should_throw_exception_when_getting_secret() {
        // Given
        final Secret unsupportedSecret = new UnsupportedSecret();

        // When && Then
        final UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> unsupportedSecret.secret());
        assertEquals("Unsupported", exception.getMessage());
    }

    @Test
    public void should_return_value_when_encrypting() {
        // Given
        final Secret unsupportedSecret = new UnsupportedSecret();
        final Encryption mockEncryption = mock(Encryption.class);

        // When
        final String encrypted = unsupportedSecret.encrypt("String to encrypt", mockEncryption);

        // Then
        assertEquals("String to encrypt", encrypted);
        verifyNoMoreInteractions(mockEncryption);
    }

    @Test
    public void should_return_value_when_decrypting() {
        // Given
        final Secret unsupportedSecret = new UnsupportedSecret();
        final Encryption mockEncryption = mock(Encryption.class);

        // When
        final String decrypted = unsupportedSecret.decrypt("String to decrypt", mockEncryption);

        // Then
        assertEquals("String to decrypt", decrypted);
        verifyNoMoreInteractions(mockEncryption);
    }
}
