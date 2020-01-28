package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

public interface EncryptedEventSecretRepository {

    EncryptedEventSecret find(String aggregateRootId, String aggregateRootType);

    EncryptedEventSecret merge(EncryptedEventSecret encryptedEventSecret);

}
