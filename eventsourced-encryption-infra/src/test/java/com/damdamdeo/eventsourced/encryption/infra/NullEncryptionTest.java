package com.damdamdeo.eventsourced.encryption.infra;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.NullEncryptionQualifier;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class NullEncryptionTest {

    @Inject
    @NullEncryptionQualifier
    Encryption nullEncryption;

    @Test
    public void should_throw_exception_when_generate_a_secret() {
        // Given

        // When
        assertThrows(UnsupportedOperationException.class, () -> nullEncryption.generateNewSecret());

        // Then
    }

    @Test
    public void should_encrypt_return_string_to_encrypt() {
        // Given
        final String strToEncrypt = "strToEncrypt";

        // When
        final String encrypted = nullEncryption.encrypt(strToEncrypt, null);

        // Then
        assertEquals("strToEncrypt", encrypted);
    }

    @Test
    public void should_decrypt_return_string_to_decrypt() {
        // Given
        final String strToDecrypt = "strToDecrypt";

        // When
        final String decrypted = nullEncryption.decrypt(strToDecrypt, null);

        // Then
        assertEquals("strToDecrypt", decrypted);
    }

}
