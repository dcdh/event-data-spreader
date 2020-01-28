package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

import com.damdamdeo.eventdataspreader.eventsourcing.api.*;

import java.util.Date;
import java.util.Objects;

public final class Event {

    private final String eventId;
    private final String aggregateRootId;
    private final String aggregateRootType;
    private final String eventType;
    private final Long version;
    private final Date creationDate;
    private final EventPayload eventPayload;
    private final EventMetadata eventMetaData;

    public Event(final DecryptableEvent decryptableEvent,
                 final EncryptedEventSecret encryptedEventSecret,
                 final EventPayloadSerializer eventPayloadSerializer,
                 final EventMetadataSerializer eventMetadataSerializer) {
        eventId = decryptableEvent.eventId();
        aggregateRootId = decryptableEvent.aggregateRootId();
        aggregateRootType = decryptableEvent.aggregateRootType();
        eventType = decryptableEvent.eventType();
        version = decryptableEvent.version();
        creationDate = decryptableEvent.creationDate();
        eventPayload = decryptableEvent.eventPayload(encryptedEventSecret, eventPayloadSerializer);
        eventMetaData = decryptableEvent.eventMetaData(encryptedEventSecret, eventMetadataSerializer);
    }

    public String eventId() {
        return eventId;
    }

    public String aggregateRootId() {
        return aggregateRootId;
    }

    public String aggregateRootType() {
        return aggregateRootType;
    }

    public String eventType() {
        return eventType;
    }

    public Long version() {
        return version;
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
                Objects.equals(aggregateRootId, event.aggregateRootId) &&
                Objects.equals(aggregateRootType, event.aggregateRootType) &&
                Objects.equals(eventType, event.eventType) &&
                Objects.equals(version, event.version) &&
                Objects.equals(creationDate, event.creationDate) &&
                Objects.equals(eventPayload, event.eventPayload) &&
                Objects.equals(eventMetaData, event.eventMetaData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, aggregateRootId, aggregateRootType, eventType, version, creationDate, eventPayload, eventMetaData);
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId='" + eventId + '\'' +
                ", aggregateRootId='" + aggregateRootId + '\'' +
                ", aggregateRootType='" + aggregateRootType + '\'' +
                ", eventType='" + eventType + '\'' +
                ", version=" + version +
                ", creationDate=" + creationDate +
                ", eventPayload=" + eventPayload +
                ", eventMetaData=" + eventMetaData +
                '}';
    }
}
