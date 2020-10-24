package com.damdamdeo.eventsourced.encryption.api;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface CryptoService<INFRA> {

    INFRA encrypt(AggregateRootId aggregateRootId,
                  String valueToEncrypt,
                  boolean shouldEncrypt) throws UnableToEncryptMissingSecretException;

    INFRA encrypt(AggregateRootId aggregateRootId,
                  Long valueToEncrypt,
                  boolean shouldEncrypt) throws UnableToEncryptMissingSecretException;

    INFRA encrypt(AggregateRootId aggregateRootId,
                  Integer valueToEncrypt,
                  boolean shouldEncrypt) throws UnableToEncryptMissingSecretException;

    INFRA encrypt(AggregateRootId aggregateRootId,
                  BigInteger valueToEncrypt,
                  boolean shouldEncrypt) throws UnableToEncryptMissingSecretException;

    INFRA encrypt(AggregateRootId aggregateRootId,
                  BigDecimal valueToEncrypt,
                  boolean shouldEncrypt) throws UnableToEncryptMissingSecretException;

    INFRA decrypt(INFRA valueDecryptable);

    INFRA recursiveDecrypt(INFRA source);

}
