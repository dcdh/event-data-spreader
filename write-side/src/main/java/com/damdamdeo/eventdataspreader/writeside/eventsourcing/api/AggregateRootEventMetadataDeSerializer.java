package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

import java.util.Optional;

public interface AggregateRootEventMetadataDeSerializer {

    String serialize(Optional<EncryptedEventSecret> encryptedEventSecret, AggregateRootEventMetadata aggregateRootEventMetadata);

    AggregateRootEventMetadata deserialize(Optional<EncryptedEventSecret> encryptedEventSecret, String eventMetadata);

}
