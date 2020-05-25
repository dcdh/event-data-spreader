package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

import java.util.Optional;

public interface AggregateRootMaterializedStateSerializer {

    String serialize(Optional<EncryptedEventSecret> encryptedEventSecret, AggregateRoot aggregateRoot);

    String serialize(AggregateRoot aggregateRoot);

}
