package com.damdamdeo.eventdataspreader.debeziumeventconsumer.api;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface EventConsumedRepository {

    void addEventConsumerConsumed(UUID eventId, Class consumerClass);

    void markEventAsConsumed(UUID eventId, Date consumedAt);

    boolean hasConsumedEvent(UUID eventId);

    List<String> getConsumedEventsForEventId(final UUID eventId);

}
