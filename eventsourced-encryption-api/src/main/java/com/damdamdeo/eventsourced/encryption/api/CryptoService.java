package com.damdamdeo.eventsourced.encryption.api;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;

public interface CryptoService<INFRA> {

    void encrypt(AggregateRootId aggregateRootId,
                 INFRA parentNode,
                 String fieldName,
                 Encryption encryption) throws UnableToEncryptMissingSecretException;

    void decrypt(INFRA parentNode,
                 String fieldName,
                 Encryption encryption);

    void recursiveDecrypt(INFRA source, Encryption encryption);

}
