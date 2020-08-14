package com.damdamdeo.eventsourced.mutable.api.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventPayloadsDeSerializer;
import org.apache.commons.lang3.Validate;

import java.time.LocalDateTime;
import java.util.Objects;

public final class AggregateRootEvent {

    private final AggregateRootEventId aggregateRootEventId;
    private final String eventType;
    private final LocalDateTime creationDate;
    private final AggregateRootEventPayload aggregateRootEventPayload;

    public AggregateRootEvent(final AggregateRootEventId aggregateRootEventId,
                              final String eventType,
                              final LocalDateTime creationDate,
                              final AggregateRootEventPayload aggregateRootEventPayload) {
        this.aggregateRootEventId = Objects.requireNonNull(aggregateRootEventId);
        this.eventType = Objects.requireNonNull(eventType);
        this.creationDate = Objects.requireNonNull(creationDate);
        this.aggregateRootEventPayload = Objects.requireNonNull(aggregateRootEventPayload);
    }

    public AggregateRootEvent(final DecryptableEvent decryptableEvent,
                              final AggregateRootEventPayloadsDeSerializer aggregateRootEventPayloadsDeSerializer) {
        Validate.notNull(decryptableEvent);
        Validate.notNull(aggregateRootEventPayloadsDeSerializer);
        this.aggregateRootEventId = decryptableEvent.eventId();
        this.eventType = decryptableEvent.eventType();
        this.creationDate = decryptableEvent.creationDate();
        this.aggregateRootEventPayload = decryptableEvent.eventPayload(aggregateRootEventPayloadsDeSerializer);
    }

    public AggregateRootEventId eventId() {
        return aggregateRootEventId;
    }

    public AggregateRootId aggregateRootId() {
        return aggregateRootEventId.aggregateRootId();
    }

    public String eventType() {
        return eventType;
    }

    public Long version() {
        return aggregateRootEventId.version();
    }

    public LocalDateTime creationDate() {
        return creationDate;
    }

    public AggregateRootEventPayload eventPayload() {
        return aggregateRootEventPayload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AggregateRootEvent that = (AggregateRootEvent) o;
        return Objects.equals(aggregateRootEventId, that.aggregateRootEventId) &&
                Objects.equals(eventType, that.eventType) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(aggregateRootEventPayload, that.aggregateRootEventPayload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootEventId, eventType, creationDate, aggregateRootEventPayload);
    }

    @Override
    public String toString() {
        return "AggregateRootEvent{" +
                "aggregateRootEventId=" + aggregateRootEventId +
                ", eventType='" + eventType + '\'' +
                ", creationDate=" + creationDate +
                '}';
    }
}
