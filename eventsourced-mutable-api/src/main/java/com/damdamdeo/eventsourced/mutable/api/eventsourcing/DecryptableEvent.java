package com.damdamdeo.eventsourced.mutable.api.eventsourcing;

import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventPayloadDeSerializer;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventMetadata;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventMetadataDeSerializer;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventPayload;

import java.time.LocalDateTime;

public interface DecryptableEvent {

    AggregateRootEventId eventId();

    String eventType();

    LocalDateTime creationDate();

    AggregateRootEventPayload<? super AggregateRoot> eventPayload(Secret secret, AggregateRootEventPayloadDeSerializer aggregateRootEventPayloadDeSerializer);

    AggregateRootEventMetadata eventMetaData(Secret secret, AggregateRootEventMetadataDeSerializer aggregateRootEventMetadataDeSerializer);

}
