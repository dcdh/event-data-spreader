package com.damdamdeo.eventdataspreader.event.infrastructure.consumer;

import com.damdamdeo.eventdataspreader.event.api.*;
import com.damdamdeo.eventdataspreader.event.api.consumer.*;
import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

import java.time.LocalDateTime;
import java.util.Optional;

public class DecryptedAggregateRootEventConsumable implements AggregateRootEventConsumable {

    private final AggregateRootEventId aggregateRootEventId;
    private final String eventType;
    private final LocalDateTime creationDate;
    private final AggregateRootEventPayloadConsumer aggregateRootEventPayloadConsumer;
    private final AggregateRootEventMetadataConsumer aggregateRootEventMetaDataConsumer;

    public DecryptedAggregateRootEventConsumable(final DecryptableAggregateRootEvent decryptableAggregateRootEvent,
                                                 final Optional<EncryptedEventSecret> encryptedEventSecret,
                                                 final AggregateRootEventMetadataConsumerDeserializer aggregateRootEventMetadataConsumerDeSerializer,
                                                 final AggregateRootEventPayloadConsumerDeserializer aggregateRootEventPayloadConsumerDeserializer) {
        aggregateRootEventId = decryptableAggregateRootEvent.eventId();
        eventType = decryptableAggregateRootEvent.eventType();
        creationDate = decryptableAggregateRootEvent.creationDate();
        aggregateRootEventPayloadConsumer = decryptableAggregateRootEvent.eventPayload(encryptedEventSecret, aggregateRootEventPayloadConsumerDeserializer);
        aggregateRootEventMetaDataConsumer = decryptableAggregateRootEvent.eventMetaData(encryptedEventSecret, aggregateRootEventMetadataConsumerDeSerializer);
    }

    @Override
    public AggregateRootEventId eventId() {
        return aggregateRootEventId;
    }

    @Override
    public String eventType() {
        return eventType;
    }

    @Override
    public LocalDateTime creationDate() {
        return creationDate;
    }

    @Override
    public AggregateRootEventPayloadConsumer eventPayload() {
        return aggregateRootEventPayloadConsumer;
    }

    @Override
    public AggregateRootEventMetadataConsumer eventMetaData() {
        return aggregateRootEventMetaDataConsumer;
    }

}
