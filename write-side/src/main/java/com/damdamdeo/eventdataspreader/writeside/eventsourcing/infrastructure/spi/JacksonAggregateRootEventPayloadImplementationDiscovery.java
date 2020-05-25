package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.spi;

import com.damdamdeo.eventdataspreader.event.infrastructure.consumer.spi.JacksonImplementationDiscovery;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.JacksonAggregateRootEventPayload;

public interface JacksonAggregateRootEventPayloadImplementationDiscovery
        extends JacksonImplementationDiscovery<AggregateRootEventPayload, JacksonAggregateRootEventPayload> {

}
