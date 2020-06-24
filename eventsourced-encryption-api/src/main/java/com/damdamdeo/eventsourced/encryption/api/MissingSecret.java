package com.damdamdeo.eventsourced.encryption.api;

public final class MissingSecret implements Secret {

    private static final String ANONYMIZED_VALUE = "*****";

    @Override
    public String secret() {
        throw new UnsupportedOperationException("Secret is missing");
    }

    @Override
    public String encrypt(final String strToEncrypt, final Encryption encryption) {
        throw new UnsupportedOperationException("Could not encrypt. Secret is missing");
    }

    @Override
    public String decrypt(final String strToDecrypt, final Encryption encryption) {
        return ANONYMIZED_VALUE;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return true;
    }

}
