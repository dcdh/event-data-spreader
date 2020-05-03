package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import com.damdamdeo.eventdataspreader.event.api.EventId;
import com.damdamdeo.eventdataspreader.event.api.EventMetadata;
import com.damdamdeo.eventdataspreader.event.api.EventMetadataDeserializer;
import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

import java.util.Date;
import java.util.Optional;

// TODO remove by using the one defined in event-api
public interface DecryptableEvent {

    EventId eventId();

    String eventType();

    Date creationDate();

    AggregateRootEventPayload eventPayload(Optional<EncryptedEventSecret> encryptedEventSecret, AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer);

    EventMetadata eventMetaData(Optional<EncryptedEventSecret> encryptedEventSecret, EventMetadataDeserializer eventMetadataDeserializer);

}
