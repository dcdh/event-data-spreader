package com.damdamdeo.eventdataspreader.event.api.consumer;

import com.damdamdeo.eventdataspreader.event.api.AggregateRootEventId;
import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DecryptableAggregateRootEvent {

    AggregateRootEventId eventId();

    String eventType();

    LocalDateTime creationDate();

    AggregateRootEventPayloadConsumer eventPayload(Optional<EncryptedEventSecret> encryptedEventSecret, AggregateRootEventPayloadConsumerDeserializer aggregateRootEventPayloadConsumerDeserializer);

    AggregateRootEventMetadataConsumer eventMetaData(Optional<EncryptedEventSecret> encryptedEventSecret, AggregateRootEventMetadataConsumerDeserializer aggregateRootEventMetadataConsumerDeSerializer);

}
