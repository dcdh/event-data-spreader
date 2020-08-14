package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import com.damdamdeo.eventsourced.encryption.api.AESEncryptionQualifier;
import com.damdamdeo.eventsourced.encryption.api.Encryption;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@AESEncryptionQualifier
public class UnsupportedAESEncryption implements Encryption {

    @Override
    public String generateNewSecret() {
        throw new UnsupportedOperationException("Must be mocked !");
    }

    @Override
    public String encrypt(final String strToEncrypt, final String secret) {
        throw new UnsupportedOperationException("Must be mocked !");
    }

    @Override
    public String decrypt(final String strToDecrypt, final String secret) {
        throw new UnsupportedOperationException("Must be mocked !");
    }
}
