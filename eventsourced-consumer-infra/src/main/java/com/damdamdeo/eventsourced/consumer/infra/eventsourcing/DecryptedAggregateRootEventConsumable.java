package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.*;
import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public final class DecryptedAggregateRootEventConsumable implements AggregateRootEventConsumable {

    private final AggregateRootEventId aggregateRootEventId;
    private final String eventType;
    private final LocalDateTime creationDate;
    private final AggregateRootEventPayloadConsumer aggregateRootEventPayloadConsumer;
    private final AggregateRootEventMetadataConsumer aggregateRootEventMetaDataConsumer;

    public DecryptedAggregateRootEventConsumable(final DebeziumAggregateRootEventConsumable decryptableAggregateRootEvent,
                                                 final Optional<AggregateRootSecret> aggregateRootSecret,
                                                 final AggregateRootEventMetadataConsumerDeserializer aggregateRootEventMetadataConsumerDeSerializer,
                                                 final AggregateRootEventPayloadConsumerDeserializer aggregateRootEventPayloadConsumerDeserializer) {
        aggregateRootEventId = decryptableAggregateRootEvent.eventId();
        eventType = decryptableAggregateRootEvent.eventType();
        creationDate = decryptableAggregateRootEvent.creationDate();
        aggregateRootEventPayloadConsumer = decryptableAggregateRootEvent.eventPayload(aggregateRootSecret, aggregateRootEventPayloadConsumerDeserializer);
        aggregateRootEventMetaDataConsumer = decryptableAggregateRootEvent.eventMetaData(aggregateRootSecret, aggregateRootEventMetadataConsumerDeSerializer);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DecryptedAggregateRootEventConsumable that = (DecryptedAggregateRootEventConsumable) o;
        return Objects.equals(aggregateRootEventId, that.aggregateRootEventId) &&
                Objects.equals(eventType, that.eventType) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(aggregateRootEventPayloadConsumer, that.aggregateRootEventPayloadConsumer) &&
                Objects.equals(aggregateRootEventMetaDataConsumer, that.aggregateRootEventMetaDataConsumer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootEventId, eventType, creationDate, aggregateRootEventPayloadConsumer, aggregateRootEventMetaDataConsumer);
    }
}
