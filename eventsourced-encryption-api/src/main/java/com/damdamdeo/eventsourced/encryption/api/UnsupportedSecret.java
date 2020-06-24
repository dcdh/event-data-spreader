package com.damdamdeo.eventsourced.encryption.api;

public final class UnsupportedSecret implements Secret {

    @Override
    public String secret() {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public String encrypt(final String strToEncrypt, final Encryption encryption) {
        return strToEncrypt;
    }

    @Override
    public String decrypt(final String strToDecrypt, final Encryption encryption) {
        return strToDecrypt;
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
