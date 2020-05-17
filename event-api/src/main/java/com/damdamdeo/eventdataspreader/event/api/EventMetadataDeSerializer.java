package com.damdamdeo.eventdataspreader.event.api;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

import java.util.Optional;

public interface EventMetadataDeSerializer {

    String serialize(Optional<EncryptedEventSecret> encryptedEventSecret, EventMetadata eventMetadata);

    EventMetadata deserialize(Optional<EncryptedEventSecret> encryptedEventSecret, String eventMetadata);

}
