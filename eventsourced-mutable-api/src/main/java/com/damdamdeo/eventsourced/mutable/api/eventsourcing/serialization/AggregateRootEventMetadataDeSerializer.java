package com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization;

import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;

import java.util.Optional;

public interface AggregateRootEventMetadataDeSerializer {

    String serialize(Optional<AggregateRootSecret> aggregateRootSecret, AggregateRootEventMetadata aggregateRootEventMetadata);

    AggregateRootEventMetadata deserialize(Optional<AggregateRootSecret> aggregateRootSecret, String eventMetadata);

}
