package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.spi;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.JacksonAggregateRootMaterializedState;

public interface JacksonAggregateRootMaterializedStateImplementationDiscovery
        extends JacksonAggregateRootImplementationDiscovery<AggregateRoot, JacksonAggregateRootMaterializedState> {

}
