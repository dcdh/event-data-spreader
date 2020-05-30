package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.spi;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.aggregaterootevent.AggregateRootEventPayload;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.JacksonAggregateRootEventPayload;

public interface JacksonAggregateRootEventPayloadImplementationDiscovery
        extends JacksonAggregateRootImplementationDiscovery<AggregateRootEventPayload, JacksonAggregateRootEventPayload> {

}
