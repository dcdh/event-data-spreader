package com.damdamdeo.eventsourced.consumer.api.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;

import java.time.LocalDateTime;
import java.util.List;

public interface AggregateRootEventConsumedRepository<S extends InfrastructureMetadata> {

    void addEventConsumerConsumed(AggregateRootEventId aggregateRootEventId, Class consumerClass, LocalDateTime consumedAt, S infrastructureMetadata, String gitCommitId);

    void markEventAsConsumed(AggregateRootEventId aggregateRootEventId, LocalDateTime consumedAt, S infrastructureMetadata);

    boolean hasFinishedConsumingEvent(AggregateRootEventId aggregateRootEventId);

    List<String> getConsumersHavingProcessedEvent(AggregateRootEventId aggregateRootEventId);

}
