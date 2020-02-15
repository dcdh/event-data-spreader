package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventId;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventMetadata;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventMetadataDeserializer;
import com.damdamdeo.eventdataspreader.eventsourcing.api.*;
import org.apache.commons.lang3.Validate;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

public final class Event {

    private final String aggregateRootId;
    private final String aggregateRootType;
    private final String eventType;
    private final Long version;
    private final Date creationDate;
    private final EventMetadata eventMetaData;
    private final AggregateRootEventPayload aggregateRootEventPayload;

    public Event(final String aggregateRootId,
                 final String aggregateRootType,
                 final String eventType,
                 final Long version,
                 final Date creationDate,
                 final AggregateRootEventPayload aggregateRootEventPayload,
                 final EventMetadata eventMetaData) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
        this.eventType = Objects.requireNonNull(eventType);
        this.version = Objects.requireNonNull(version);
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
        this.aggregateRootId = decryptableEvent.aggregateRootId();
        this.aggregateRootType = decryptableEvent.aggregateRootType();
        this.eventType = decryptableEvent.eventType();
        this.version = decryptableEvent.eventId().version();
        this.creationDate = decryptableEvent.creationDate();
        this.aggregateRootEventPayload = decryptableEvent.eventPayload(encryptedEventSecret, aggregateRootEventPayloadDeSerializer);
        this.eventMetaData = decryptableEvent.eventMetaData(encryptedEventSecret, eventMetadataDeserializer);
    }

    public EventId eventId() {
        return new EventId() {

            @Override
            public String aggregateRootId() {
                return aggregateRootId;
            }

            @Override
            public String aggregateRootType() {
                return aggregateRootType;
            }

            @Override
            public Long version() {
                return version;
            }
        };
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
        return Objects.equals(aggregateRootId, event.aggregateRootId) &&
                Objects.equals(aggregateRootType, event.aggregateRootType) &&
                Objects.equals(eventType, event.eventType) &&
                Objects.equals(version, event.version) &&
                Objects.equals(creationDate, event.creationDate) &&
                Objects.equals(eventMetaData, event.eventMetaData) &&
                Objects.equals(aggregateRootEventPayload, event.aggregateRootEventPayload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootId, aggregateRootType, eventType, version, creationDate, eventMetaData, aggregateRootEventPayload);
    }

    @Override
    public String toString() {
        return "Event{" +
                "aggregateRootId='" + aggregateRootId + '\'' +
                ", aggregateRootType='" + aggregateRootType + '\'' +
                ", eventType='" + eventType + '\'' +
                ", version=" + version +
                ", creationDate=" + creationDate +
                ", eventMetaData=" + eventMetaData +
                ", aggregateRootEventPayload=" + aggregateRootEventPayload +
                '}';
    }
}
