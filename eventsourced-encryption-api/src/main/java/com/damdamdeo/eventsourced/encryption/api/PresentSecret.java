package com.damdamdeo.eventsourced.encryption.api;

import java.util.Objects;

public final class PresentSecret implements Secret {

    private final String secret;

    public PresentSecret(final String secret) {
        this.secret = Objects.requireNonNull(secret);
    }

    @Override
    public String secret() {
        return secret;
    }

    @Override
    public String encrypt(final String strToEncrypt, final Encryption encryption) {
        return encryption.encrypt(strToEncrypt, secret);
    }

    @Override
    public String decrypt(final String strToDecrypt, final Encryption encryption) {
        return encryption.decrypt(strToDecrypt, secret);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PresentSecret that = (PresentSecret) o;
        return Objects.equals(secret, that.secret);
    }

    @Override
    public int hashCode() {
        return Objects.hash(secret);
    }
}
