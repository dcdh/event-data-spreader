package com.damdamdeo.eventsourced.encryption.infra;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class AESEncryptionTest {

    @Inject
    AESEncryption aesEncryption;

    @Test
    public void should_generate_a_32_alpha_secret() {
        // Given

        // When
        final String secret = aesEncryption.generateNewSecret();

        // Then
        // tester via une regexp
        assertTrue(secret.matches("[a-zA-Z]{32}"));
    }

    @Test
    public void should_encrypt_decrypt_message_return_message() {
        // Given
        final String secret = aesEncryption.generateNewSecret();

        // When
        final String encryptedMessage = aesEncryption.encrypt("Hello World", secret);
        final String decryptedMessage = aesEncryption.decrypt(encryptedMessage, secret);

        // Then
        assertEquals("Hello World", decryptedMessage);
    }

}
