package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import com.damdamdeo.eventdataspreader.event.api.EventId;
import com.damdamdeo.eventdataspreader.event.api.EventMetadata;
import com.damdamdeo.eventdataspreader.event.api.EventMetadataDeserializer;
import com.damdamdeo.eventdataspreader.eventsourcing.api.*;
import org.apache.commons.lang3.Validate;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public final class Event {

    private final EventId eventId;
    private final String eventType;
    private final LocalDateTime creationDate;
    private final EventMetadata eventMetaData;
    private final AggregateRootEventPayload aggregateRootEventPayload;

    public Event(final EventId eventId,
                 final String eventType,
                 final LocalDateTime creationDate,
                 final AggregateRootEventPayload aggregateRootEventPayload,
                 final EventMetadata eventMetaData) {
        this.eventId = Objects.requireNonNull(eventId);
        this.eventType = Objects.requireNonNull(eventType);
        this.creationDate = Objects.requireNonNull(creationDate);
        this.aggregateRootEventPayload = Objects.requireNonNull(aggregateRootEventPayload);
        this.eventMetaData = Objects.requireNonNull(eventMetaData);
    }

    public Event(final DecryptableEvent decryptableEvent,
                 final Optional<EncryptedEventSecret> encryptedEventSecret,
                 final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer,
                 final EventMetadataDeserializer eventMetadataDeserializer) {
        Validate.notNull(decryptableEvent);
        Validate.notNull(encryptedEventSecret);
        Validate.notNull(aggregateRootEventPayloadDeSerializer);
        Validate.notNull(eventMetadataDeserializer);
        this.eventId = decryptableEvent.eventId();
        this.eventType = decryptableEvent.eventType();
        this.creationDate = decryptableEvent.creationDate();
        this.aggregateRootEventPayload = decryptableEvent.eventPayload(encryptedEventSecret, aggregateRootEventPayloadDeSerializer);
        this.eventMetaData = decryptableEvent.eventMetaData(encryptedEventSecret, eventMetadataDeserializer);
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

    public LocalDateTime creationDate() {
        return creationDate;
    }

    public AggregateRootEventPayload eventPayload() {
        return aggregateRootEventPayload;
    }

    public EventMetadata eventMetaData() {
        return eventMetaData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(eventId, event.eventId) &&
                Objects.equals(eventType, event.eventType) &&
                Objects.equals(creationDate, event.creationDate) &&
                Objects.equals(eventMetaData, event.eventMetaData) &&
                Objects.equals(aggregateRootEventPayload, event.aggregateRootEventPayload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, eventType, creationDate, eventMetaData, aggregateRootEventPayload);
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", eventType='" + eventType + '\'' +
                ", creationDate=" + creationDate +
                ", eventMetaData=" + eventMetaData +
                ", aggregateRootEventPayload=" + aggregateRootEventPayload +
                '}';
    }
}
