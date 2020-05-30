package com.damdamdeo.eventsourced.encryption.infra;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.EncryptionException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.Validate;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.ApplicationScoped;
import java.util.Base64;

@ApplicationScoped
public class AESEncryption implements Encryption {

    public static final int SECRET_LENGTH = 32;

    private static final String AES = "AES";
    private static final String CIPHER_TRANSFORMATION = AES + "/ECB/PKCS5Padding";
    private static final String UTF_8 = "UTF-8";

    @Override
    public String generateNewSecret() {
        return RandomStringUtils.randomAlphabetic(SECRET_LENGTH);
    }

    @Override
    public String encrypt(final String strToEncrypt, final String secret) {
        Validate.validState(secret.length() == SECRET_LENGTH);
        try {
            final SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(UTF_8), AES);
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
            final SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(UTF_8), AES);
            final Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (final Exception e) {
            throw new EncryptionException(e);
        }
    }

}
