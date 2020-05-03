package com.damdamdeo.eventdataspreader.event.api;

import com.damdamdeo.eventdataspreader.eventsourcing.api.*;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

public final class Event {

    private final EventId eventId;
    private final String eventType;
    private final Date creationDate;
    private final EventPayload eventPayload;
    private final EventMetadata eventMetaData;

    public Event(final DecryptableEvent decryptableEvent,
                 final Optional<EncryptedEventSecret> encryptedEventSecret,
                 final EventMetadataDeserializer eventMetadataDeserializer,
                 final EventPayloadDeserializer eventPayloadDeserializer) {
        eventId = decryptableEvent.eventId();
        eventType = decryptableEvent.eventType();
        creationDate = decryptableEvent.creationDate();
        eventPayload = decryptableEvent.eventPayload(encryptedEventSecret, eventPayloadDeserializer);
        eventMetaData = decryptableEvent.eventMetaData(encryptedEventSecret, eventMetadataDeserializer);
    }

    public EventId eventId() {
        return eventId;
    }

    public String aggregateRootId() {
        return eventId.aggregateRootId();
    }

    public String aggregateRootType() {
        return eventId.aggregateRootType();
    }

    public String eventType() {
        return eventType;
    }

    public Long version() {
        return eventId.version();
    }

    public Date creationDate() {
        return creationDate;
    }

    public EventPayload eventPayload() {
        return eventPayload;
    }

    public EventMetadata eventMetaData() {
        return eventMetaData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return Objects.equals(eventId, event.eventId) &&
                Objects.equals(eventType, event.eventType) &&
                Objects.equals(creationDate, event.creationDate) &&
                Objects.equals(eventPayload, event.eventPayload) &&
                Objects.equals(eventMetaData, event.eventMetaData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, eventType, creationDate, eventPayload, eventMetaData);
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", eventType='" + eventType + '\'' +
                ", creationDate=" + creationDate +
                ", eventPayload=" + eventPayload +
                ", eventMetaData=" + eventMetaData +
                '}';
    }
}
