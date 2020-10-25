package com.damdamdeo.eventsourced.encryption.api;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface CryptoService<VALUE, OBJECT> {

    VALUE encrypt(AggregateRootId aggregateRootId,
                  String valueToEncrypt,
                  boolean shouldEncrypt) throws UnableToEncryptMissingSecretException;

    VALUE encrypt(AggregateRootId aggregateRootId,
                  Long valueToEncrypt,
                  boolean shouldEncrypt) throws UnableToEncryptMissingSecretException;

    VALUE encrypt(AggregateRootId aggregateRootId,
                  Integer valueToEncrypt,
                  boolean shouldEncrypt) throws UnableToEncryptMissingSecretException;

    VALUE encrypt(AggregateRootId aggregateRootId,
                  BigInteger valueToEncrypt,
                  boolean shouldEncrypt) throws UnableToEncryptMissingSecretException;

    VALUE encrypt(AggregateRootId aggregateRootId,
                  BigDecimal valueToEncrypt,
                  boolean shouldEncrypt) throws UnableToEncryptMissingSecretException;

    VALUE decrypt(VALUE valueDecryptable);

    OBJECT recursiveDecrypt(OBJECT source);

}
