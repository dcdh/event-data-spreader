package com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.spi;

import com.damdamdeo.eventdataspreader.event.infrastructure.consumer.spi.JacksonImplementationDiscovery;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.JacksonAggregateRootMaterializedState;

public interface JacksonAggregateRootMaterializedStateImplementationDiscovery
        extends JacksonImplementationDiscovery<AggregateRoot, JacksonAggregateRootMaterializedState> {

}
