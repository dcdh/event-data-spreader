package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.spi;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventMetadata;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.JacksonAggregateRootEventMetadata;

public interface JacksonAggregateRootEventMetadataImplementationDiscovery
        extends JacksonAggregateRootImplementationDiscovery<AggregateRootEventMetadata, JacksonAggregateRootEventMetadata> {

}
