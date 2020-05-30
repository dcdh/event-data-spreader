package com.damdamdeo.eventsourced.mutable.api.eventsourcing.aggregaterootevent;

import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;

import java.util.Optional;

public interface AggregateRootEventPayloadDeSerializer {

    String serialize(Optional<AggregateRootSecret> aggregateRootSecret, AggregateRootEventPayload aggregateRootEventPayload);

    AggregateRootEventPayload deserialize(Optional<AggregateRootSecret> aggregateRootSecret, String eventPayload);

}
