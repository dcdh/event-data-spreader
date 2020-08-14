package com.damdamdeo.eventsourced.encryption.api;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;

public interface Secret {

    String secret();

    String encrypt(AggregateRootId aggregateRootId, String strToEncrypt, Encryption encryption)
            throws UnableToEncryptMissingSecretException;

    String decrypt(AggregateRootId aggregateRootId, String strToDecrypt, Encryption encryption)
            throws UnableToDecryptMissingSecretException;

}
