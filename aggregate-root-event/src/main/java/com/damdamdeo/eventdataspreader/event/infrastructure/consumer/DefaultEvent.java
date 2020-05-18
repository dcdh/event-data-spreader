package com.damdamdeo.eventdataspreader.event.infrastructure.consumer;

import com.damdamdeo.eventdataspreader.event.api.*;
import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;

import java.time.LocalDateTime;
import java.util.Optional;

// TODO better naming !
public class DefaultEvent implements Event {

    private final EventId eventId;
    private final String eventType;
    private final LocalDateTime creationDate;
    private final EventPayload eventPayload;
    private final EventMetadata eventMetaData;

    public DefaultEvent(final DecryptableEvent decryptableEvent,
                        final Optional<EncryptedEventSecret> encryptedEventSecret,
                        final EventMetadataDeSerializer eventMetadataDeSerializer,
                        final EventPayloadDeserializer eventPayloadDeserializer) {
        eventId = decryptableEvent.eventId();
        eventType = decryptableEvent.eventType();
        creationDate = decryptableEvent.creationDate();
        eventPayload = decryptableEvent.eventPayload(encryptedEventSecret, eventPayloadDeserializer);
        eventMetaData = decryptableEvent.eventMetaData(encryptedEventSecret, eventMetadataDeSerializer);
    }

    @Override
    public EventId eventId() {
        return eventId;
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
    public EventPayload eventPayload() {
        return eventPayload;
    }

    @Override
    public EventMetadata eventMetaData() {
        return eventMetaData;
    }

}
