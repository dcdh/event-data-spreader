package com.damdamdeo.eventsourced.encryption.api;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;

public interface CryptService<INFRA> {

    void encrypt(final AggregateRootId aggregateRootId,
                 final INFRA parentNode,
                 final String fieldName,
                 final Encryption encryption) throws UnableToEncryptMissingSecretException;

    void decrypt(final INFRA parentNode,
                 final String fieldName,
                 final Encryption encryption);

}
