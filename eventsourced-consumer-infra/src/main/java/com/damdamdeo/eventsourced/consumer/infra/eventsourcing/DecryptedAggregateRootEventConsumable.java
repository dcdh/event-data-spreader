package com.damdamdeo.eventsourced.consumer.infra.eventsourcing;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.*;
import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.damdamdeo.eventsourced.encryption.api.Secret;

import java.time.LocalDateTime;
import java.util.Objects;

public final class DecryptedAggregateRootEventConsumable implements AggregateRootEventConsumable {

    private final AggregateRootEventId aggregateRootEventId;
    private final String eventType;
    private final LocalDateTime creationDate;
    private final AggregateRootEventPayloadConsumer aggregateRootEventPayloadConsumer;
    private final AggregateRootEventMetadataConsumer aggregateRootEventMetaDataConsumer;
    private final AggregateRootMaterializedStateConsumer aggregateRootMaterializedStateConsumer;

    public DecryptedAggregateRootEventConsumable(final AggregateRootEventId aggregateRootEventId,
                                                 final String eventType,
                                                 final LocalDateTime creationDate,
                                                 final AggregateRootEventPayloadConsumer aggregateRootEventPayloadConsumer,
                                                 final AggregateRootEventMetadataConsumer aggregateRootEventMetaDataConsumer,
                                                 final AggregateRootMaterializedStateConsumer aggregateRootMaterializedStateConsumer) {
        this.aggregateRootEventId = Objects.requireNonNull(aggregateRootEventId);
        this.eventType = Objects.requireNonNull(eventType);
        this.creationDate = Objects.requireNonNull(creationDate);
        this.aggregateRootEventPayloadConsumer = Objects.requireNonNull(aggregateRootEventPayloadConsumer);
        this.aggregateRootEventMetaDataConsumer = Objects.requireNonNull(aggregateRootEventMetaDataConsumer);
        this.aggregateRootMaterializedStateConsumer = Objects.requireNonNull(aggregateRootMaterializedStateConsumer);
    }

    public DecryptedAggregateRootEventConsumable(final DebeziumAggregateRootEventConsumable decryptableAggregateRootEvent,
                                                 final Secret secret,
                                                 final AggregateRootEventMetadataConsumerDeserializer aggregateRootEventMetadataConsumerDeSerializer,
                                                 final AggregateRootEventPayloadConsumerDeserializer aggregateRootEventPayloadConsumerDeserializer,
                                                 final AggregateRootMaterializedStateConsumerDeserializer aggregateRootMaterializedStateConsumerDeserializer) {
        this(
                decryptableAggregateRootEvent.eventId(),
                decryptableAggregateRootEvent.eventType(),
                decryptableAggregateRootEvent.creationDate(),
                decryptableAggregateRootEvent.eventPayload(secret, aggregateRootEventPayloadConsumerDeserializer),
                decryptableAggregateRootEvent.eventMetaData(secret, aggregateRootEventMetadataConsumerDeSerializer),
                decryptableAggregateRootEvent.materializedState(secret, aggregateRootMaterializedStateConsumerDeserializer)
        );
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
    public AggregateRootMaterializedStateConsumer materializedState() {
        return aggregateRootMaterializedStateConsumer;
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
                Objects.equals(aggregateRootEventMetaDataConsumer, that.aggregateRootEventMetaDataConsumer) &&
                Objects.equals(aggregateRootMaterializedStateConsumer, that.aggregateRootMaterializedStateConsumer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootEventId, eventType, creationDate, aggregateRootEventPayloadConsumer, aggregateRootEventMetaDataConsumer, aggregateRootMaterializedStateConsumer);
    }

    @Override
    public String toString() {
        return "DecryptedAggregateRootEventConsumable{" +
                "aggregateRootEventId=" + aggregateRootEventId +
                ", eventType='" + eventType + '\'' +
                ", creationDate=" + creationDate +
                ", aggregateRootEventPayloadConsumer=" + aggregateRootEventPayloadConsumer +
                ", aggregateRootEventMetaDataConsumer=" + aggregateRootEventMetaDataConsumer +
                ", aggregateRootMaterializedStateConsumer=" + aggregateRootMaterializedStateConsumer +
                '}';
    }
}