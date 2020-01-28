package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

public interface AggregateRootSerializer {

    String serialize(EncryptedEventSecret encryptedEventSecret, AggregateRoot aggregateRoot);

    AggregateRoot deserialize(EncryptedEventSecret encryptedEventSecret, String aggregateRoot);

}
