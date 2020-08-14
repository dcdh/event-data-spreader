package com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRootEventPayload;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.UnsupportedAggregateRootEventPayload;

public interface AggregateRootEventPayloadsDeSerializer {

    String serialize(String aggregateRootType, String eventType, AggregateRootEventPayload aggregateRootEventPayload)
            throws UnsupportedAggregateRootEventPayload;

    AggregateRootEventPayload deserialize(String aggregateRootType, String eventType, String eventPayload)
            throws UnsupportedAggregateRootEventPayload;

}
