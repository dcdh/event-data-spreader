package com.damdamdeo.eventdataspreader.eventsourcing.infrastructure;

import com.damdamdeo.eventdataspreader.eventsourcing.api.Encryption;
import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptionException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.Validate;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class AESEncryption implements Encryption {

    public static final int SECRET_LENGTH = 32;

    private static final String CIPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String UTF_8 = "UTF-8";
    private static final String SHA_1 = "SHA-1";
    private static final String AES = "AES";

    @Override
    public String generateNewSecret() {
        return RandomStringUtils.randomAlphabetic(SECRET_LENGTH);
    }

    @Override
    public String encrypt(final String strToEncrypt, final String secret) {
        Validate.validState(secret.length() == SECRET_LENGTH);
        try {
            final SecretKeySpec secretKey = createSecretKeySpecFromSecret(secret);
            final Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(UTF_8)));
        } catch (final Exception e) {
            throw new EncryptionException(e);
        }
    }

    @Override
    public String decrypt(final String strToDecrypt, final String secret) {
        Validate.validState(secret.length() == SECRET_LENGTH);
        try {
            final SecretKeySpec secretKey = createSecretKeySpecFromSecret(secret);
            final Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (final Exception e) {
            throw new EncryptionException(e);
        }
    }

    private SecretKeySpec createSecretKeySpecFromSecret(final String mySecret) throws Exception {
        final MessageDigest sha = MessageDigest.getInstance(SHA_1);
        byte[] secret = mySecret.getBytes(UTF_8);
        secret = sha.digest(secret);
        secret = Arrays.copyOf(secret, SECRET_LENGTH);
        return new SecretKeySpec(secret, AES);
    }

}
