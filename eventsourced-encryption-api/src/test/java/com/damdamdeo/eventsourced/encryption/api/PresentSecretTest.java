package com.damdamdeo.eventsourced.encryption.api;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PresentSecretTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(PresentSecret.class).verify();
    }

    @Test
    public void should_return_secret() {
        // Given
        final Secret presentSecret = new PresentSecret("mon secret");

        // When
        final String secret = presentSecret.secret();

        // Then
        Assertions.assertEquals("mon secret", secret);
    }

    @Test
    public void should_call_encryption_encrypt_when_encrypting() {
        // Given
        final Secret presentSecret = new PresentSecret("mon secret");
        final Encryption mockEncryption = mock(Encryption.class);
        doReturn("encrypted").when(mockEncryption).encrypt("String to encrypt", "mon secret");

        // When
        final String encrypted = presentSecret.encrypt("String to encrypt", mockEncryption);

        // Then
        assertEquals("encrypted", encrypted);
        verify(mockEncryption, times(1)).encrypt(any(), any());
    }

    @Test
    public void should_call_encryption_decrypt_when_decrypting() {
        // Given
        final Secret presentSecret = new PresentSecret("mon secret");
        final Encryption mockEncryption = mock(Encryption.class);
        doReturn("decrypted").when(mockEncryption).decrypt("String to decrypt", "mon secret");

        // When
        final String decrypted = presentSecret.decrypt("String to decrypt", mockEncryption);

        // Then
        assertEquals("decrypted", decrypted);
        verify(mockEncryption, times(1)).decrypt(any(), any());
    }

}
