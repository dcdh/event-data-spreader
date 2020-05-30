package com.damdamdeo.eventsourced.mutable.api.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.aggregaterootevent.AggregateRootEventPayloadDeSerializer;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.aggregaterootevent.AggregateRootEventMetadata;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.aggregaterootevent.AggregateRootEventMetadataDeSerializer;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.aggregaterootevent.AggregateRootEventPayload;
import org.apache.commons.lang3.Validate;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public final class AggregateRootEvent {

    private final AggregateRootEventId aggregateRootEventId;
    private final String eventType;
    private final LocalDateTime creationDate;
    private final AggregateRootEventMetadata aggregateRootEventMetaData;
    private final AggregateRootEventPayload aggregateRootEventPayload;

    public AggregateRootEvent(final AggregateRootEventId aggregateRootEventId,
                              final String eventType,
                              final LocalDateTime creationDate,
                              final AggregateRootEventPayload aggregateRootEventPayload,
                              final AggregateRootEventMetadata aggregateRootEventMetaData) {
        this.aggregateRootEventId = Objects.requireNonNull(aggregateRootEventId);
        this.eventType = Objects.requireNonNull(eventType);
        this.creationDate = Objects.requireNonNull(creationDate);
        this.aggregateRootEventPayload = Objects.requireNonNull(aggregateRootEventPayload);
        this.aggregateRootEventMetaData = Objects.requireNonNull(aggregateRootEventMetaData);
    }

    public AggregateRootEvent(final DecryptableEvent decryptableEvent,
                              final Optional<AggregateRootSecret> aggregateRootSecret,
                              final AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer,
                              final AggregateRootEventMetadataDeSerializer aggregateRootEventMetadataDeSerializer) {
        Validate.notNull(decryptableEvent);
        Validate.notNull(aggregateRootSecret);
        Validate.notNull(aggregateRootEventPayloadDeSerializer);
        Validate.notNull(aggregateRootEventMetadataDeSerializer);
        this.aggregateRootEventId = decryptableEvent.eventId();
        this.eventType = decryptableEvent.eventType();
        this.creationDate = decryptableEvent.creationDate();
        this.aggregateRootEventPayload = decryptableEvent.eventPayload(aggregateRootSecret, aggregateRootEventPayloadDeSerializer);
        this.aggregateRootEventMetaData = decryptableEvent.eventMetaData(aggregateRootSecret, aggregateRootEventMetadataDeSerializer);
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

    public AggregateRootEventMetadata eventMetaData() {
        return aggregateRootEventMetaData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AggregateRootEvent that = (AggregateRootEvent) o;
        return Objects.equals(aggregateRootEventId, that.aggregateRootEventId) &&
                Objects.equals(eventType, that.eventType) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(aggregateRootEventMetaData, that.aggregateRootEventMetaData) &&
                Objects.equals(aggregateRootEventPayload, that.aggregateRootEventPayload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootEventId, eventType, creationDate, aggregateRootEventMetaData, aggregateRootEventPayload);
    }

    @Override
    public String toString() {
        return "AggregateRootEvent{" +
                "aggregateRootEventId=" + aggregateRootEventId +
                ", eventType='" + eventType + '\'' +
                ", creationDate=" + creationDate +
                ", aggregateRootEventMetaData=" + aggregateRootEventMetaData +
                ", aggregateRootEventPayload=" + aggregateRootEventPayload +
                '}';
    }
}
