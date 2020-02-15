package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

import java.util.Optional;

public interface AggregateRootEventPayloadDeSerializer {

    String serialize(Optional<EncryptedEventSecret> encryptedEventSecret, AggregateRootEventPayload aggregateRootEventPayload);

    AggregateRootEventPayload deserialize(Optional<EncryptedEventSecret> encryptedEventSecret, String eventPayload);

}
