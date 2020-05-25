package com.damdamdeo.eventdataspreader.event.api.consumer;

import com.damdamdeo.eventdataspreader.event.api.AggregateRootEventId;

import java.time.LocalDateTime;

public interface AggregateRootEventConsumable {

    AggregateRootEventId eventId();

    String eventType();

    LocalDateTime creationDate();

    AggregateRootEventPayloadConsumer eventPayload();

    AggregateRootEventMetadataConsumer eventMetaData();

}
