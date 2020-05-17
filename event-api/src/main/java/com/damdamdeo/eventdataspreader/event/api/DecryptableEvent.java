package com.damdamdeo.eventdataspreader.event.api;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DecryptableEvent {

    EventId eventId();

    String eventType();

    LocalDateTime creationDate();

    EventPayload eventPayload(Optional<EncryptedEventSecret> encryptedEventSecret, EventPayloadDeserializer eventPayloadDeserializer);

    EventMetadata eventMetaData(Optional<EncryptedEventSecret> encryptedEventSecret, EventMetadataDeSerializer eventMetadataDeSerializer);

}
