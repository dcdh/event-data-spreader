package com.damdamdeo.eventsourced.encryption.api;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;

public interface CryptoService<INFRA> {

    void encrypt(AggregateRootId aggregateRootId,
                 INFRA parentNode,
                 String fieldName,
                 boolean shouldEncrypt) throws UnableToEncryptMissingSecretException;

    void decrypt(INFRA parentNode, String fieldName);

    void recursiveDecrypt(INFRA source);

}
