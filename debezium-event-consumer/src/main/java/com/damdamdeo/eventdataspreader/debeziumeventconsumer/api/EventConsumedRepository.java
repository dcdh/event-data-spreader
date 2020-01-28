package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

import java.util.Date;
import java.util.List;

public interface EventConsumedRepository {

    void addEventConsumerConsumed(String eventId, Class consumerClass, KafkaSource kafkaSource, String gitCommitId);

    void markEventAsConsumed(String eventId, Date consumedAt, KafkaSource kafkaSource);

    boolean hasConsumedEvent(String eventId);

    List<String> getConsumedEventsForEventId(String eventId);

}
