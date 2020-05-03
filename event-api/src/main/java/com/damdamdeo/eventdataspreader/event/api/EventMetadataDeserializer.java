package com.damdamdeo.eventdataspreader.event.api;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

import java.util.Optional;

public interface EventMetadataDeserializer {

    EventMetadata deserialize(Optional<EncryptedEventSecret> encryptedEventSecret, String eventMetadata);

}
