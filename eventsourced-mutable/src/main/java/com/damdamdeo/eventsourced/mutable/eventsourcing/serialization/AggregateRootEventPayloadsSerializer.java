package com.damdamdeo.eventsourced.mutable.eventsourcing.serialization;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.eventsourcing.AggregateRootEventPayload;
import com.damdamdeo.eventsourced.mutable.eventsourcing.UnsupportedAggregateRootEventPayload;

public interface AggregateRootEventPayloadsSerializer {

    String aggregateRootType();

    String eventType();

    String serialize(AggregateRootId aggregateRootId, AggregateRootEventPayload aggregateRootEventPayload) throws UnsupportedAggregateRootEventPayload;

}
