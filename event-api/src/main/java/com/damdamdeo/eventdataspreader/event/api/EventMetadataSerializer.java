package com.damdamdeo.eventdataspreader.event.api;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

import java.util.Optional;

public interface EventMetadataSerializer {

    String serialize(Optional<EncryptedEventSecret> encryptedEventSecret, EventMetadata eventMetadata);

}
