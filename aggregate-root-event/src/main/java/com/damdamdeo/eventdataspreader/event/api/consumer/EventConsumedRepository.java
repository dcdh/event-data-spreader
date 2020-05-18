package com.damdamdeo.eventdataspreader.event.api.consumer;

import com.damdamdeo.eventdataspreader.event.api.EventId;

import java.time.LocalDateTime;
import java.util.List;

public interface EventConsumedRepository<S extends Source> {

    void addEventConsumerConsumed(EventId eventId, Class consumerClass, LocalDateTime consumedAt, S source, String gitCommitId);

    void markEventAsConsumed(EventId eventId, LocalDateTime consumedAt, S source);

    boolean hasFinishedConsumingEvent(EventId eventId);

    List<String> getConsumersHavingProcessedEvent(EventId eventId);

}
