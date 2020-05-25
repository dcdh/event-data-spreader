package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import com.damdamdeo.eventdataspreader.event.api.AggregateRootEventId;
import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

import java.time.LocalDateTime;
import java.util.Optional;

// TODO remove by using the one defined in event-api
public interface DecryptableEvent {

    AggregateRootEventId eventId();

    String eventType();

    LocalDateTime creationDate();

    AggregateRootEventPayload eventPayload(Optional<EncryptedEventSecret> encryptedEventSecret, AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer);

    AggregateRootEventMetadata eventMetaData(Optional<EncryptedEventSecret> encryptedEventSecret, AggregateRootEventMetadataDeSerializer aggregateRootEventMetadataDeSerializer);

}
