package com.damdamdeo.eventsourced.encryption.infra;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.NullEncryptionQualifier;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@NullEncryptionQualifier
public class NullEncryption implements Encryption {

    @Override
    public String generateNewSecret() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String encrypt(final String strToEncrypt, final String secret) {
        return strToEncrypt;
    }

    @Override
    public String decrypt(final String strToDecrypt, final String secret) {
        return strToDecrypt;
    }

}
