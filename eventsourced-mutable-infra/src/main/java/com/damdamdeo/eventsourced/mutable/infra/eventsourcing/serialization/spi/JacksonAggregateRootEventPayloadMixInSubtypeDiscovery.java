package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.spi;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventPayload;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.JacksonAggregateRootEventPayload;

public interface JacksonAggregateRootEventPayloadMixInSubtypeDiscovery
        extends JacksonMixInSubtypeDiscovery<AggregateRootEventPayload, JacksonAggregateRootEventPayload> {

}
