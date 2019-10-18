package com.damdamdeo.eventdataspreader.writeside.eventsourcing.api;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class Event {

    private final UUID eventId;
    private final String aggregateRootId;
    private final String aggregateRootType;
    private final String eventType;
    private final Long version;
    private final Date creationDate;
    private final EventMetadata eventMetaData;
    private final EventPayload eventPayload;

    public Event(final UUID eventId,
                 final Long version,
                 final Date creationDate,
                 final EventPayload eventPayload,
                 final EventMetadata eventMetaData) {
        this.eventId = Objects.requireNonNull(eventId);
        this.version = Objects.requireNonNull(version);
        this.creationDate = Objects.requireNonNull(creationDate);
        this.eventPayload = Objects.requireNonNull(eventPayload);
        this.eventMetaData = Objects.requireNonNull(eventMetaData);
        this.aggregateRootId = Objects.requireNonNull(eventPayload.eventPayloadIdentifier().aggregateRootId());
        this.aggregateRootType = Objects.requireNonNull(eventPayload.eventPayloadIdentifier().aggregateRootType());
        this.eventType = Objects.requireNonNull(eventPayload.eventPayloadIdentifier()
                .eventPayloadType()
                .replaceFirst("(^.+)Payload$", "$1"));
    }

    public UUID eventId() {
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

}
