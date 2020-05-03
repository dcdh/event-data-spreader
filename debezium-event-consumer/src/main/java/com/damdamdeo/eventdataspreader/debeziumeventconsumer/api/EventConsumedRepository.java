package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

import com.damdamdeo.eventdataspreader.event.api.EventId;

import java.util.Date;
import java.util.List;

public interface EventConsumedRepository {

    void addEventConsumerConsumed(EventId eventId, Class consumerClass, KafkaSource kafkaSource, String gitCommitId);

    void markEventAsConsumed(EventId eventId, Date consumedAt, KafkaSource kafkaSource);

    boolean hasConsumedEvent(EventId eventId);

    List<String> getConsumedEventsForEventId(EventId eventId);

}
