package com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRootEventPayload;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.UnsupportedAggregateRootEventPayload;

public interface AggregateRootEventPayloadsDeSerializer {

    String serialize(AggregateRootId aggregateRootId, String eventType, AggregateRootEventPayload aggregateRootEventPayload)
            throws UnsupportedAggregateRootEventPayload;

    AggregateRootEventPayload deserialize(String aggregateRootType, String eventType, String eventPayload)
            throws UnsupportedAggregateRootEventPayload;

}
