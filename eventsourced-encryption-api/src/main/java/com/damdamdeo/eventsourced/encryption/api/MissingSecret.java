package com.damdamdeo.eventsourced.encryption.api;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;

public final class MissingSecret implements Secret {

    @Override
    public String secret() {
        throw new UnsupportedOperationException("Secret is missing");
    }

    @Override
    public String encrypt(final AggregateRootId aggregateRootId,
                          final String strToEncrypt,
                          final Encryption encryption) throws UnableToEncryptMissingSecretException {
        throw new UnableToEncryptMissingSecretException(aggregateRootId);
    }

    @Override
    public String decrypt(final AggregateRootId aggregateRootId,
                          final String strToDecrypt,
                          final Encryption encryption) throws UnableToDecryptMissingSecretException {
        throw new UnableToDecryptMissingSecretException(aggregateRootId);
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
