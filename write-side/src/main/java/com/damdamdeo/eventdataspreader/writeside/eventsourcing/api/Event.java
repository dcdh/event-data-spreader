package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventMetadata;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventMetadataSerializer;
import com.damdamdeo.eventdataspreader.eventsourcing.api.*;
import org.apache.commons.lang3.Validate;

import java.util.Date;
import java.util.Objects;

public final class Event {

    private final String eventId;
    private final String aggregateRootId;
    private final String aggregateRootType;
    private final String eventType;
    private final Long version;
    private final Date creationDate;
    private final EventMetadata eventMetaData;
    private final AggregateRootEventPayload aggregateRootEventPayload;

    public Event(final String eventId,
                 final String aggregateRootId,
                 final String aggregateRootType,
                 final String eventType,
                 final Long version,
                 final Date creationDate,
                 final AggregateRootEventPayload aggregateRootEventPayload,
                 final EventMetadata eventMetaData) {
        this.eventId = Objects.requireNonNull(eventId);
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
        this.eventType = Objects.requireNonNull(eventType);
        this.version = Objects.requireNonNull(version);
        this.creationDate = Objects.requireNonNull(creationDate);
        this.aggregateRootEventPayload = Objects.requireNonNull(aggregateRootEventPayload);
        this.eventMetaData = Objects.requireNonNull(eventMetaData);
    }

    public Event(final DecryptableEvent decryptableEvent,
                 final EncryptedEventSecret encryptedEventSecret,
                 final AggregateRootEventPayloadSerializer aggregateRootEventPayloadSerializer,
                 final EventMetadataSerializer eventMetadataSerializer) {
        Validate.notNull(decryptableEvent);
        Validate.notNull(encryptedEventSecret);
        Validate.notNull(aggregateRootEventPayloadSerializer);
        Validate.notNull(eventMetadataSerializer);
        this.eventId = decryptableEvent.eventId();
        this.aggregateRootId = decryptableEvent.aggregateRootId();
        this.aggregateRootType = decryptableEvent.aggregateRootType();
        this.eventType = decryptableEvent.eventType();
        this.version = decryptableEvent.version();
        this.creationDate = decryptableEvent.creationDate();
        this.aggregateRootEventPayload = decryptableEvent.eventPayload(encryptedEventSecret, aggregateRootEventPayloadSerializer);
        this.eventMetaData = decryptableEvent.eventMetaData(encryptedEventSecret, eventMetadataSerializer);
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

    public AggregateRootEventPayload eventPayload() {
        return aggregateRootEventPayload;
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
                Objects.equals(eventMetaData, event.eventMetaData) &&
                Objects.equals(aggregateRootEventPayload, event.aggregateRootEventPayload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, aggregateRootId, aggregateRootType, eventType, version, creationDate, eventMetaData, aggregateRootEventPayload);
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
                ", eventMetaData=" + eventMetaData +
                ", aggregateRootEventPayload=" + aggregateRootEventPayload +
                '}';
    }
}
