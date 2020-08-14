package com.damdamdeo.eventsourced.consumer.api.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;

import java.time.LocalDateTime;

public interface AggregateRootEventConsumable<INFRA> {

    AggregateRootEventId eventId();

    String eventType();

    LocalDateTime creationDate();

    INFRA eventPayload();

    INFRA eventMetaData();

    INFRA materializedState();

}
