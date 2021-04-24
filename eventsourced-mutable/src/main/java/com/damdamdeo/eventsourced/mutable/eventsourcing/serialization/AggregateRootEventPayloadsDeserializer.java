package com.damdamdeo.eventsourced.mutable.eventsourcing.serialization;

import com.damdamdeo.eventsourced.mutable.eventsourcing.AggregateRootEventPayload;
import com.damdamdeo.eventsourced.mutable.eventsourcing.UnsupportedAggregateRootEventPayload;

public interface AggregateRootEventPayloadsDeserializer<EVENT_PAYLOAD> {

    String aggregateRootType();

    String eventType();

    AggregateRootEventPayload deserialize(EVENT_PAYLOAD eventPayload) throws UnsupportedAggregateRootEventPayload;

}
