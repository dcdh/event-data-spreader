package com.damdamdeo.eventsourced.consumer.api.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DecryptableAggregateRootEvent {

    AggregateRootEventId eventId();

    String eventType();

    LocalDateTime creationDate();

    AggregateRootEventPayloadConsumer eventPayload(Optional<AggregateRootSecret> aggregateRootSecret, AggregateRootEventPayloadConsumerDeserializer aggregateRootEventPayloadConsumerDeserializer);

    AggregateRootEventMetadataConsumer eventMetaData(Optional<AggregateRootSecret> aggregateRootSecret, AggregateRootEventMetadataConsumerDeserializer aggregateRootEventMetadataConsumerDeSerializer);

}
