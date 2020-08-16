package com.damdamdeo.eventsourced.mutable.publisher;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.NullEncryptionQualifier;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@NullEncryptionQualifier
public class UnsupportedNullEncryption implements Encryption {

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
