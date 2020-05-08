package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

import com.damdamdeo.eventdataspreader.event.api.EventId;

import java.time.LocalDateTime;
import java.util.List;

public interface EventConsumedRepository {

    void addEventConsumerConsumed(EventId eventId, Class consumerClass, LocalDateTime consumedAt, KafkaSource kafkaSource, String gitCommitId);

    void markEventAsConsumed(EventId eventId, LocalDateTime consumedAt, KafkaSource kafkaSource);

    boolean hasFinishedConsumingEvent(EventId eventId);

    List<String> getConsumersHavingProcessedEvent(EventId eventId);

}
