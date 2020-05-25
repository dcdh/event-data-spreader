package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.spi;

import com.damdamdeo.eventdataspreader.event.infrastructure.consumer.spi.JacksonImplementationDiscovery;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventMetadata;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.JacksonAggregateRootEventMetadata;

public interface JacksonAggregateRootEventMetadataImplementationDiscovery
        extends JacksonImplementationDiscovery<AggregateRootEventMetadata, JacksonAggregateRootEventMetadata> {

}
