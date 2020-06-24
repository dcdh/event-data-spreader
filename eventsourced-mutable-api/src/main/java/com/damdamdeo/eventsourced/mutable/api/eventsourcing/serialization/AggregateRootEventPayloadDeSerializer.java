package com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization;

import com.damdamdeo.eventsourced.encryption.api.Secret;

public interface AggregateRootEventPayloadDeSerializer {

    String serialize(Secret secret, AggregateRootEventPayload aggregateRootEventPayload);

    AggregateRootEventPayload deserialize(Secret secret, String eventPayload);

}
