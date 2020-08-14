package com.damdamdeo.eventsourced.mutable.api.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventPayloadsDeSerializer;

import java.time.LocalDateTime;

public interface DecryptableEvent {

    AggregateRootEventId eventId();

    String eventType();

    LocalDateTime creationDate();

    AggregateRootEventPayload<? super AggregateRoot> eventPayload(AggregateRootEventPayloadsDeSerializer aggregateRootEventPayloadsDeSerializer);

}
