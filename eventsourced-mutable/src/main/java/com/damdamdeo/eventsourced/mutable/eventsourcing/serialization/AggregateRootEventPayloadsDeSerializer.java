package com.damdamdeo.eventsourced.mutable.eventsourcing.serialization;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.eventsourcing.AggregateRootEventPayload;
import com.damdamdeo.eventsourced.mutable.eventsourcing.UnsupportedAggregateRootEventPayload;

putain extraire en 2 !!!
putain à virer !!!
public interface AggregateRootEventPayloadsDeSerializer {

    String aggregateRootType();

    String eventType();

    String serialize(AggregateRootId aggregateRootId, AggregateRootEventPayload aggregateRootEventPayload) throws UnsupportedAggregateRootEventPayload;

    AggregateRootEventPayload deserialize(String eventPayload) throws UnsupportedAggregateRootEventPayload;

}
