package com.damdamdeo.eventdataspreader.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.eventsourcing.api.Encryption;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AESEncryptionTest {

    @Test
    public void should_generate_a_32_alpha_secret() {
        // Given
        final Encryption encryption = new AESEncryption();

        // When
        final String secret = encryption.generateNewSecret();

        // Then
        // tester via une regexp
        assertTrue(secret.matches("[a-zA-Z]{32}"));
    }

    @Test
    public void should_encrypt_decrypt_message_return_message() {
        // Given
        final Encryption encryption = new AESEncryption();
        final String secret = encryption.generateNewSecret();

        // When
        final String encryptedMessage = encryption.encrypt("Hello World", secret);
        final String decryptedMessage = encryption.decrypt(encryptedMessage, secret);

        // Then
        assertEquals("Hello World", decryptedMessage);
    }

}
