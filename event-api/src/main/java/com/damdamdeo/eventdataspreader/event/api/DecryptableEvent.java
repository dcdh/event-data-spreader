package com.damdamdeo.eventdataspreader.event.api;

import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

import java.util.Date;
import java.util.Optional;

public interface DecryptableEvent {

    EventId eventId();

    String eventType();

    Date creationDate();

    EventPayload eventPayload(Optional<EncryptedEventSecret> encryptedEventSecret, EventPayloadDeserializer eventPayloadDeserializer);

    EventMetadata eventMetaData(Optional<EncryptedEventSecret> encryptedEventSecret, EventMetadataDeserializer eventMetadataDeserializer);

}
