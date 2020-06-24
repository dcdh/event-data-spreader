package com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization;

import com.damdamdeo.eventsourced.encryption.api.Secret;

public interface AggregateRootEventMetadataDeSerializer {

    String serialize(Secret secret, AggregateRootEventMetadata aggregateRootEventMetadata);

    AggregateRootEventMetadata deserialize(Secret secret, String eventMetadata);

}
