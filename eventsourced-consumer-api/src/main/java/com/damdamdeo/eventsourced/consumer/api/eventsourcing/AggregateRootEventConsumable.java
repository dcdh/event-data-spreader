package com.damdamdeo.eventsourced.consumer.api.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;

import java.time.LocalDateTime;

public interface AggregateRootEventConsumable {

    AggregateRootEventId eventId();

    String eventType();

    LocalDateTime creationDate();

    AggregateRootEventPayloadConsumer eventPayload();

    AggregateRootEventMetadataConsumer eventMetaData();

    AggregateRootMaterializedStateConsumer materializedState();

}
