package com.damdamdeo.eventdataspreader.event.api.consumer;

import com.damdamdeo.eventdataspreader.event.api.AggregateRootEventId;

import java.time.LocalDateTime;
import java.util.List;

public interface AggregateRootEventConsumedRepository<S extends InfrastructureMetadata> {

    void addEventConsumerConsumed(AggregateRootEventId aggregateRootEventId, Class consumerClass, LocalDateTime consumedAt, S infrastructureMetadata, String gitCommitId);

    void markEventAsConsumed(AggregateRootEventId aggregateRootEventId, LocalDateTime consumedAt, S infrastructureMetadata);

    boolean hasFinishedConsumingEvent(AggregateRootEventId aggregateRootEventId);

    List<String> getConsumersHavingProcessedEvent(AggregateRootEventId aggregateRootEventId);

}
