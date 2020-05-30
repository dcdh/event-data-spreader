package com.damdamdeo.eventsourced.consumer.infra;

import com.damdamdeo.eventsourced.encryption.api.Encryption;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UnsupportedEncryption implements Encryption {

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
