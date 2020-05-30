package com.damdamdeo.eventsourced.mutable.api.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.aggregaterootevent.AggregateRootEventPayloadDeSerializer;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.aggregaterootevent.AggregateRootEventMetadata;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.aggregaterootevent.AggregateRootEventMetadataDeSerializer;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.aggregaterootevent.AggregateRootEventPayload;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DecryptableEvent {

    AggregateRootEventId eventId();

    String eventType();

    LocalDateTime creationDate();

    AggregateRootEventPayload<? super AggregateRoot> eventPayload(Optional<AggregateRootSecret> aggregateRootSecret, AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer);

    AggregateRootEventMetadata eventMetaData(Optional<AggregateRootSecret> aggregateRootSecret, AggregateRootEventMetadataDeSerializer aggregateRootEventMetadataDeSerializer);

}
